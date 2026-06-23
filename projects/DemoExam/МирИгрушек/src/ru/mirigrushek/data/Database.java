package ru.mirigrushek.data;

import ru.mirigrushek.app.AppConfig;
import ru.mirigrushek.model.OrderData;
import ru.mirigrushek.model.OrderItemInput;
import ru.mirigrushek.model.OrderSummary;
import ru.mirigrushek.model.Product;
import ru.mirigrushek.model.ProductInput;
import ru.mirigrushek.model.ReferenceItem;
import ru.mirigrushek.model.UserSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Database {
    private final AppConfig config;

    public Database(AppConfig config) {
        this.config = config;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(
                config.databaseUrl(),
                config.databaseUser(),
                config.databasePassword()
        );
    }

    public void checkConnection() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {
            result.next();
        }
    }

    public UserSession authenticate(String login, String password) throws SQLException {
        String sql = """
                SELECT users.id, users.full_name, users.login, roles.code, roles.name
                FROM users
                JOIN roles ON roles.id = users.role_id
                WHERE lower(users.login) = lower(?) AND users.password = ?
                """;
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login.strip());
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                return new UserSession(
                        result.getLong(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5)
                );
            }
        }
    }

    public List<ReferenceItem> listReferences(String table) throws SQLException {
        if (!List.of("categories", "suppliers", "manufacturers", "units", "pickup_points").contains(table)) {
            throw new IllegalArgumentException("Недопустимый справочник.");
        }
        String column = "pickup_points".equals(table) ? "address" : "name";
        String sql = "SELECT id, " + column + " FROM " + table + " ORDER BY id";
        List<ReferenceItem> items = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                items.add(new ReferenceItem(result.getLong(1), result.getString(2)));
            }
        }
        return items;
    }

    public List<ReferenceItem> listClients() throws SQLException {
        String sql = """
                SELECT users.id, users.full_name
                FROM users
                JOIN roles ON roles.id = users.role_id
                WHERE roles.code = 'client'
                ORDER BY users.id
                """;
        List<ReferenceItem> clients = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                clients.add(new ReferenceItem(result.getLong(1), result.getString(2)));
            }
        }
        return clients;
    }

    public List<Product> listProducts(
            String search,
            Long supplierId,
            String sortField,
            boolean descending
    ) throws SQLException {
        String sortColumn = switch (sortField) {
            case "price" -> "products.price";
            case "stock" -> "products.stock";
            default -> "products.name";
        };
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();
        String text = search == null ? "" : search.strip();
        if (!text.isEmpty()) {
            for (String term : text.split("\\s+")) {
                conditions.add("""
                        concat_ws(' ', products.article, products.name, products.description,
                        categories.name, suppliers.name, manufacturers.name, units.name) ILIKE ?
                        """);
                parameters.add("%" + term + "%");
            }
        }
        if (supplierId != null) {
            conditions.add("products.supplier_id = ?");
            parameters.add(supplierId);
        }
        String where = conditions.isEmpty() ? "" : "WHERE " + String.join(" AND ", conditions);
        String direction = descending ? "DESC" : "ASC";
        String sql = """
                SELECT products.id, products.article, products.name,
                       units.id, units.name, products.price,
                       suppliers.id, suppliers.name,
                       manufacturers.id, manufacturers.name,
                       categories.id, categories.name,
                       products.discount, products.stock, products.description,
                       products.image_path
                FROM products
                JOIN units ON units.id = products.unit_id
                JOIN suppliers ON suppliers.id = products.supplier_id
                JOIN manufacturers ON manufacturers.id = products.manufacturer_id
                JOIN categories ON categories.id = products.category_id
                %s
                ORDER BY %s %s, products.id
                """.formatted(where, sortColumn, direction);
        List<Product> products = new ArrayList<>();
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(readProduct(result));
                }
            }
        }
        return products;
    }

    public Product getProduct(long productId) throws SQLException {
        String sql = """
                SELECT products.id, products.article, products.name,
                       units.id, units.name, products.price,
                       suppliers.id, suppliers.name,
                       manufacturers.id, manufacturers.name,
                       categories.id, categories.name,
                       products.discount, products.stock, products.description,
                       products.image_path
                FROM products
                JOIN units ON units.id = products.unit_id
                JOIN suppliers ON suppliers.id = products.supplier_id
                JOIN manufacturers ON manufacturers.id = products.manufacturer_id
                JOIN categories ON categories.id = products.category_id
                WHERE products.id = ?
                """;
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? readProduct(result) : null;
            }
        }
    }

    public long createProduct(ProductInput product) throws SQLException {
        String sql = """
                INSERT INTO products(
                    article, name, unit_id, price, supplier_id, manufacturer_id,
                    category_id, discount, stock, description, image_path
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindProduct(statement, product);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    public void updateProduct(long productId, ProductInput product) throws SQLException {
        String sql = """
                UPDATE products
                SET article = ?, name = ?, unit_id = ?, price = ?, supplier_id = ?,
                    manufacturer_id = ?, category_id = ?, discount = ?, stock = ?,
                    description = ?, image_path = ?
                WHERE id = ?
                """;
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindProduct(statement, product);
            statement.setLong(12, productId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Редактируемый товар не найден.");
            }
        }
    }

    public void deleteProduct(long productId) throws SQLException {
        String checkSql = "SELECT 1 FROM order_items WHERE product_id = ? LIMIT 1";
        String deleteSql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = connect();
             PreparedStatement check = connection.prepareStatement(checkSql);
             PreparedStatement delete = connection.prepareStatement(deleteSql)) {
            check.setLong(1, productId);
            try (ResultSet result = check.executeQuery()) {
                if (result.next()) {
                    throw new SQLException("Товар присутствует в заказе и не может быть удалён.");
                }
            }
            delete.setLong(1, productId);
            if (delete.executeUpdate() == 0) {
                throw new SQLException("Удаляемый товар не найден.");
            }
        }
    }

    public List<OrderSummary> listOrders() throws SQLException {
        String sql = """
                SELECT orders.id, orders.order_number,
                       string_agg(products.article || ' × ' || order_items.quantity, ', ' ORDER BY order_items.id),
                       orders.order_date, orders.delivery_date, pickup_points.address,
                       users.full_name, orders.pickup_code, trim(orders.status)
                FROM orders
                JOIN pickup_points ON pickup_points.id = orders.pickup_point_id
                JOIN users ON users.id = orders.user_id
                JOIN order_items ON order_items.order_id = orders.id
                JOIN products ON products.id = order_items.product_id
                GROUP BY orders.id, pickup_points.address, users.full_name
                ORDER BY orders.order_number
                """;
        List<OrderSummary> orders = new ArrayList<>();
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) {
                orders.add(new OrderSummary(
                        result.getLong(1),
                        result.getInt(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getString(6),
                        result.getString(7),
                        result.getString(8),
                        result.getString(9)
                ));
            }
        }
        return orders;
    }

    public OrderData getOrder(long orderId) throws SQLException {
        String orderSql = """
                SELECT id, order_number, order_date, delivery_date, pickup_point_id,
                       user_id, pickup_code, status
                FROM orders
                WHERE id = ?
                """;
        String itemsSql = """
                SELECT products.id, products.article, order_items.quantity
                FROM order_items
                JOIN products ON products.id = order_items.product_id
                WHERE order_items.order_id = ?
                ORDER BY order_items.id
                """;
        try (Connection connection = connect();
             PreparedStatement orderStatement = connection.prepareStatement(orderSql);
             PreparedStatement itemsStatement = connection.prepareStatement(itemsSql)) {
            orderStatement.setLong(1, orderId);
            try (ResultSet result = orderStatement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }
                List<OrderItemInput> items = new ArrayList<>();
                itemsStatement.setLong(1, orderId);
                try (ResultSet itemResult = itemsStatement.executeQuery()) {
                    while (itemResult.next()) {
                        items.add(new OrderItemInput(
                                itemResult.getLong(1),
                                itemResult.getString(2),
                                itemResult.getInt(3)
                        ));
                    }
                }
                return new OrderData(
                        result.getLong(1),
                        result.getInt(2),
                        result.getString(3),
                        result.getString(4),
                        result.getLong(5),
                        result.getLong(6),
                        result.getString(7),
                        result.getString(8),
                        items
                );
            }
        }
    }

    public int nextOrderNumber() throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COALESCE(MAX(order_number), 0) + 1 FROM orders")) {
            result.next();
            return result.getInt(1);
        }
    }

    public long saveOrder(OrderData order) throws SQLException {
        String insertSql = """
                INSERT INTO orders(
                    order_number, order_date, delivery_date, pickup_point_id,
                    user_id, pickup_code, status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;
        String updateSql = """
                UPDATE orders
                SET order_number = ?, order_date = ?, delivery_date = ?, pickup_point_id = ?,
                    user_id = ?, pickup_code = ?, status = ?
                WHERE id = ?
                """;
        String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
        String insertItemSql = "INSERT INTO order_items(order_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            try {
                long orderId;
                if (order.id() == 0) {
                    try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                        bindOrder(statement, order);
                        try (ResultSet result = statement.executeQuery()) {
                            result.next();
                            orderId = result.getLong(1);
                        }
                    }
                } else {
                    try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                        bindOrder(statement, order);
                        statement.setLong(8, order.id());
                        if (statement.executeUpdate() == 0) {
                            throw new SQLException("Редактируемый заказ не найден.");
                        }
                    }
                    orderId = order.id();
                    try (PreparedStatement statement = connection.prepareStatement(deleteItemsSql)) {
                        statement.setLong(1, orderId);
                        statement.executeUpdate();
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(insertItemSql)) {
                    for (OrderItemInput item : order.items()) {
                        statement.setLong(1, orderId);
                        statement.setLong(2, item.productId());
                        statement.setInt(3, item.quantity());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                connection.commit();
                return orderId;
            } catch (SQLException error) {
                connection.rollback();
                throw error;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void deleteOrder(long orderId) throws SQLException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM orders WHERE id = ?")) {
            statement.setLong(1, orderId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Удаляемый заказ не найден.");
            }
        }
    }

    private Product readProduct(ResultSet result) throws SQLException {
        BigDecimal price = result.getBigDecimal(6);
        int discount = result.getInt(13);
        BigDecimal multiplier = BigDecimal.valueOf(100 - discount)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal finalPrice = price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        return new Product(
                result.getLong(1),
                result.getString(2),
                result.getString(3),
                new ReferenceItem(result.getLong(4), result.getString(5)),
                price,
                new ReferenceItem(result.getLong(7), result.getString(8)),
                new ReferenceItem(result.getLong(9), result.getString(10)),
                new ReferenceItem(result.getLong(11), result.getString(12)),
                discount,
                result.getInt(14),
                result.getString(15),
                result.getString(16),
                finalPrice
        );
    }

    private void bindProduct(PreparedStatement statement, ProductInput product) throws SQLException {
        statement.setString(1, product.article());
        statement.setString(2, product.name());
        statement.setLong(3, product.unitId());
        statement.setBigDecimal(4, product.price());
        statement.setLong(5, product.supplierId());
        statement.setLong(6, product.manufacturerId());
        statement.setLong(7, product.categoryId());
        statement.setInt(8, product.discount());
        statement.setInt(9, product.stock());
        statement.setString(10, product.description());
        statement.setString(11, product.imagePath());
    }

    private void bindOrder(PreparedStatement statement, OrderData order) throws SQLException {
        statement.setInt(1, order.orderNumber());
        statement.setString(2, order.orderDate());
        statement.setString(3, order.deliveryDate());
        statement.setLong(4, order.pickupPointId());
        statement.setLong(5, order.userId());
        statement.setString(6, order.pickupCode());
        statement.setString(7, order.status());
    }

    public ProductInput validateProductInput(
            String article,
            String name,
            ReferenceItem unit,
            String priceText,
            ReferenceItem supplier,
            ReferenceItem manufacturer,
            ReferenceItem category,
            String discountText,
            String stockText,
            String description,
            String imagePath
    ) {
        if (article == null || article.isBlank()) {
            throw new IllegalArgumentException("Поле «Артикул» обязательно для заполнения.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Поле «Наименование» обязательно для заполнения.");
        }
        if (unit == null || supplier == null || manufacturer == null || category == null) {
            throw new IllegalArgumentException("Выберите значения во всех выпадающих списках.");
        }
        try {
            BigDecimal price = new BigDecimal(priceText.replace(',', '.')).setScale(2, RoundingMode.HALF_UP);
            int discount = Integer.parseInt(discountText);
            int stock = Integer.parseInt(stockText);
            if (price.signum() < 0) {
                throw new IllegalArgumentException("Цена не может быть отрицательной.");
            }
            if (stock < 0) {
                throw new IllegalArgumentException("Количество на складе не может быть отрицательным.");
            }
            if (discount < 0 || discount > 100) {
                throw new IllegalArgumentException("Скидка должна быть в диапазоне от 0 до 100.");
            }
            return new ProductInput(
                    article.strip().toUpperCase(Locale.ROOT),
                    name.strip(),
                    unit.id(),
                    price,
                    supplier.id(),
                    manufacturer.id(),
                    category.id(),
                    discount,
                    stock,
                    description == null ? "" : description.strip(),
                    imagePath
            );
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Цена, количество и скидка должны быть числами.");
        }
    }
}

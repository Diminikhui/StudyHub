package ru.mirigrushek.ui;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.OrderData;
import ru.mirigrushek.model.OrderItemInput;
import ru.mirigrushek.model.Product;
import ru.mirigrushek.model.ReferenceItem;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class OrderDialog extends JDialog {
    private final Database database;
    private final Long orderId;
    private final JTextField numberField = new JTextField();
    private final JTextField orderDateField = new JTextField();
    private final JTextField deliveryDateField = new JTextField();
    private final JComboBox<ReferenceItem> pickupBox = new JComboBox<>();
    private final JComboBox<ReferenceItem> clientBox = new JComboBox<>();
    private final JTextField codeField = new JTextField();
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"Новый", "Завершен"});
    private final JTextArea itemsArea = new JTextArea(8, 36);
    private final Map<String, Product> productsByArticle = new LinkedHashMap<>();
    private boolean saved;

    public OrderDialog(Window owner, Database database, Long orderId) {
        super(owner, orderId == null ? "Добавление заказа" : "Редактирование заказа", Dialog.ModalityType.APPLICATION_MODAL);
        this.database = database;
        this.orderId = orderId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(720, 620));
        setSize(760, 650);
        setLocationRelativeTo(owner);
        buildInterface();
        loadData();
    }

    public boolean isSaved() {
        return saved;
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(AppColors.PRIMARY_BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(6, 6, 6, 6);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        int row = 0;
        addField(fields, constraints, row++, "Номер заказа", numberField);
        addField(fields, constraints, row++, "Дата заказа", orderDateField);
        addField(fields, constraints, row++, "Дата доставки", deliveryDateField);
        addField(fields, constraints, row++, "Пункт выдачи", pickupBox);
        addField(fields, constraints, row++, "Авторизированный клиент", clientBox);
        addField(fields, constraints, row++, "Код для получения", codeField);
        addField(fields, constraints, row++, "Статус заказа", statusBox);

        itemsArea.setLineWrap(true);
        itemsArea.setWrapStyleWord(true);
        addField(fields, constraints, row, "Состав заказа", new JScrollPane(itemsArea));

        JLabel hint = new JLabel("<html>Каждая строка: <b>артикул, количество</b><br>Пример: PMEZMH, 2</html>");
        constraints.gridx = 1;
        constraints.gridy = row + 1;
        fields.add(hint, constraints);
        root.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton back = Ui.button("Назад");
        JButton save = Ui.button("Сохранить");
        back.addActionListener(event -> dispose());
        save.addActionListener(event -> save());
        buttons.add(back);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void addField(
            JPanel panel,
            GridBagConstraints constraints,
            int row,
            String label,
            java.awt.Component field
    ) {
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel(label), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel.add(field, constraints);
    }

    private void loadData() {
        try {
            fillCombo(pickupBox, database.listReferences("pickup_points"));
            fillCombo(clientBox, database.listClients());
            for (Product product : database.listProducts("", null, "name", false)) {
                productsByArticle.put(product.article(), product);
            }
            if (orderId == null) {
                numberField.setText(String.valueOf(database.nextOrderNumber()));
                statusBox.setSelectedItem("Новый");
                return;
            }
            OrderData order = database.getOrder(orderId);
            if (order == null) {
                Ui.showError(this, "Заказ не найден.");
                dispose();
                return;
            }
            numberField.setText(String.valueOf(order.orderNumber()));
            orderDateField.setText(order.orderDate());
            deliveryDateField.setText(order.deliveryDate());
            select(pickupBox, order.pickupPointId());
            select(clientBox, order.userId());
            codeField.setText(order.pickupCode());
            statusBox.setSelectedItem(order.status().strip());
            StringBuilder lines = new StringBuilder();
            for (OrderItemInput item : order.items()) {
                if (!lines.isEmpty()) {
                    lines.append(System.lineSeparator());
                }
                lines.append(item.article()).append(", ").append(item.quantity());
            }
            itemsArea.setText(lines.toString());
        } catch (Exception error) {
            Ui.showError(this, error);
            dispose();
        }
    }

    private void fillCombo(JComboBox<ReferenceItem> box, List<ReferenceItem> items) {
        box.removeAllItems();
        for (ReferenceItem item : items) {
            box.addItem(item);
        }
    }

    private void select(JComboBox<ReferenceItem> box, long id) {
        for (int index = 0; index < box.getItemCount(); index++) {
            if (box.getItemAt(index).id() == id) {
                box.setSelectedIndex(index);
                return;
            }
        }
    }

    private void save() {
        try {
            int number = Integer.parseInt(numberField.getText().strip());
            String orderDate = required(orderDateField.getText(), "Дата заказа");
            String deliveryDate = required(deliveryDateField.getText(), "Дата доставки");
            String pickupCode = required(codeField.getText(), "Код для получения");
            ReferenceItem pickup = (ReferenceItem) pickupBox.getSelectedItem();
            ReferenceItem client = (ReferenceItem) clientBox.getSelectedItem();
            String status = (String) statusBox.getSelectedItem();
            if (number <= 0) {
                throw new IllegalArgumentException("Номер заказа должен быть положительным.");
            }
            if (pickup == null || client == null || status == null) {
                throw new IllegalArgumentException("Заполните все поля заказа.");
            }
            List<OrderItemInput> items = parseItems();
            OrderData order = new OrderData(
                    orderId == null ? 0 : orderId,
                    number,
                    orderDate,
                    deliveryDate,
                    pickup.id(),
                    client.id(),
                    pickupCode,
                    status,
                    items
            );
            database.saveOrder(order);
            saved = true;
            dispose();
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }

    private List<OrderItemInput> parseItems() {
        Map<Long, OrderItemInput> unique = new LinkedHashMap<>();
        for (String line : itemsArea.getText().split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Строка состава заказа должна иметь формат: артикул, количество.");
            }
            String article = parts[0].strip().toUpperCase();
            Product product = productsByArticle.get(article);
            if (product == null) {
                throw new IllegalArgumentException("Товар с артикулом " + article + " не найден.");
            }
            int quantity;
            try {
                quantity = Integer.parseInt(parts[1].strip());
            } catch (NumberFormatException error) {
                throw new IllegalArgumentException("Количество товара " + article + " должно быть целым числом.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Количество товара " + article + " должно быть больше нуля.");
            }
            unique.put(product.id(), new OrderItemInput(product.id(), product.article(), quantity));
        }
        if (unique.isEmpty()) {
            throw new IllegalArgumentException("Добавьте хотя бы один товар в заказ.");
        }
        return new ArrayList<>(unique.values());
    }

    private String required(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле «" + label + "» обязательно для заполнения.");
        }
        return value.strip();
    }
}

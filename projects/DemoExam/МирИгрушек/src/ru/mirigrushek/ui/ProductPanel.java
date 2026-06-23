package ru.mirigrushek.ui;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.Product;
import ru.mirigrushek.model.ReferenceItem;
import ru.mirigrushek.model.UserSession;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.ImageService;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

public final class ProductPanel extends JPanel {
    private final Database database;
    private final ImageService imageService;
    private final UserSession session;
    private final JPanel productList;
    private final JTextField searchField;
    private final JComboBox<SupplierChoice> supplierBox;
    private final JComboBox<SortChoice> sortBox;
    private final Timer reloadTimer;

    public ProductPanel(Database database, ImageService imageService, UserSession session) {
        super(new BorderLayout(10, 10));
        this.database = database;
        this.imageService = imageService;
        this.session = session;
        setBackground(AppColors.PRIMARY_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField(24);
        supplierBox = new JComboBox<>();
        sortBox = new JComboBox<>();
        reloadTimer = new Timer(180, event -> reloadProducts());
        reloadTimer.setRepeats(false);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        JLabel title = new JLabel("Список товаров");
        Ui.titleFont(title, 22);
        north.add(title, BorderLayout.WEST);

        if (session.canManageCatalogView()) {
            north.add(createControls(), BorderLayout.CENTER);
        }
        if (session.isAdmin()) {
            JButton addButton = Ui.button("Добавить товар");
            addButton.addActionListener(event -> openProductDialog(null));
            north.add(addButton, BorderLayout.EAST);
        }
        add(north, BorderLayout.NORTH);

        productList = new JPanel();
        productList.setLayout(new BoxLayout(productList, BoxLayout.Y_AXIS));
        productList.setBackground(AppColors.PRIMARY_BACKGROUND);

        JScrollPane scroll = new JScrollPane(productList);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        loadSuppliers();
        reloadProducts();
    }

    private JPanel createControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        controls.setOpaque(false);
        searchField.setToolTipText("Поиск по артикулу, названию, описанию, категории, поставщику и производителю");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                scheduleReload();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                scheduleReload();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                scheduleReload();
            }
        });

        sortBox.addItem(new SortChoice("По наименованию", "name", false));
        sortBox.addItem(new SortChoice("Цена по возрастанию", "price", false));
        sortBox.addItem(new SortChoice("Цена по убыванию", "price", true));
        sortBox.addItem(new SortChoice("Остаток по возрастанию", "stock", false));
        sortBox.addItem(new SortChoice("Остаток по убыванию", "stock", true));
        sortBox.addActionListener(event -> scheduleReload());
        supplierBox.addActionListener(event -> scheduleReload());

        controls.add(new JLabel("Поиск:"));
        controls.add(searchField);
        controls.add(new JLabel("Поставщик:"));
        controls.add(supplierBox);
        controls.add(new JLabel("Сортировка:"));
        controls.add(sortBox);
        return controls;
    }

    private void loadSuppliers() {
        if (!session.canManageCatalogView()) {
            return;
        }
        try {
            List<SupplierChoice> choices = new ArrayList<>();
            choices.add(new SupplierChoice(null, "Все поставщики"));
            for (ReferenceItem supplier : database.listReferences("suppliers")) {
                choices.add(new SupplierChoice(supplier.id(), supplier.name()));
            }
            supplierBox.removeAllItems();
            for (SupplierChoice choice : choices) {
                supplierBox.addItem(choice);
            }
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }

    public void reloadProducts() {
        try {
            String search = session.canManageCatalogView() ? searchField.getText() : "";
            SupplierChoice supplierChoice = session.canManageCatalogView()
                    ? (SupplierChoice) supplierBox.getSelectedItem()
                    : null;
            SortChoice sortChoice = session.canManageCatalogView()
                    ? (SortChoice) sortBox.getSelectedItem()
                    : new SortChoice("", "name", false);
            Long supplierId = supplierChoice == null ? null : supplierChoice.id();
            List<Product> products = database.listProducts(
                    search,
                    supplierId,
                    sortChoice == null ? "name" : sortChoice.field(),
                    sortChoice != null && sortChoice.descending()
            );
            productList.removeAll();
            if (products.isEmpty()) {
                JLabel empty = new JLabel("Товары не найдены.");
                empty.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
                productList.add(empty);
            } else {
                for (Product product : products) {
                    ProductCard card = new ProductCard(product, imageService, session.isAdmin());
                    if (session.isAdmin()) {
                        card.setEditAction(() -> openProductDialog(product.id()));
                        card.setDeleteAction(() -> deleteProduct(product));
                    }
                    productList.add(card);
                }
            }
            productList.revalidate();
            productList.repaint();
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }

    private void scheduleReload() {
        reloadTimer.restart();
    }

    private void openProductDialog(Long productId) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductDialog dialog = new ProductDialog(owner, database, imageService, productId);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            reloadProducts();
        }
    }

    private void deleteProduct(Product product) {
        boolean confirmed = Ui.confirm(
                this,
                "Удалить товар «" + product.name() + "»?\nОперацию невозможно отменить."
        );
        if (!confirmed) {
            return;
        }
        try {
            database.deleteProduct(product.id());
            imageService.deleteReplacedImage(product.imagePath());
            reloadProducts();
            Ui.showInfo(this, "Товар удалён.");
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }

    private record SupplierChoice(Long id, String name) {
        @Override
        public String toString() {
            return name;
        }
    }

    private record SortChoice(String name, String field, boolean descending) {
        @Override
        public String toString() {
            return name;
        }
    }
}

package ru.mirigrushek.ui;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.Product;
import ru.mirigrushek.model.ProductInput;
import ru.mirigrushek.model.ReferenceItem;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.ImageService;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.nio.file.Path;
import java.util.List;

public final class ProductDialog extends JDialog {
    private final Database database;
    private final ImageService imageService;
    private final Long productId;
    private final JTextField idField = new JTextField();
    private final JTextField articleField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JComboBox<ReferenceItem> categoryBox = new JComboBox<>();
    private final JTextArea descriptionArea = new JTextArea(4, 28);
    private final JComboBox<ReferenceItem> manufacturerBox = new JComboBox<>();
    private final JComboBox<ReferenceItem> supplierBox = new JComboBox<>();
    private final JTextField priceField = new JTextField();
    private final JComboBox<ReferenceItem> unitBox = new JComboBox<>();
    private final JTextField stockField = new JTextField();
    private final JTextField discountField = new JTextField();
    private final JLabel imagePreview = new JLabel();
    private String imagePath;
    private String originalImagePath;
    private Path selectedImage;
    private boolean saved;

    public ProductDialog(
            Window owner,
            Database database,
            ImageService imageService,
            Long productId
    ) {
        super(owner, productId == null ? "Добавление товара" : "Редактирование товара", Dialog.ModalityType.APPLICATION_MODAL);
        this.database = database;
        this.imageService = imageService;
        this.productId = productId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 720));
        setSize(820, 760);
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
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        int row = 0;
        if (productId != null) {
            idField.setEditable(false);
            addField(fields, constraints, row++, "ID товара", idField);
        }
        addField(fields, constraints, row++, "Артикул", articleField);
        addField(fields, constraints, row++, "Наименование", nameField);
        addField(fields, constraints, row++, "Категория", categoryBox);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        addField(fields, constraints, row++, "Описание", new JScrollPane(descriptionArea));
        addField(fields, constraints, row++, "Производитель", manufacturerBox);
        addField(fields, constraints, row++, "Поставщик", supplierBox);
        addField(fields, constraints, row++, "Цена", priceField);
        addField(fields, constraints, row++, "Единица измерения", unitBox);
        addField(fields, constraints, row++, "Количество на складе", stockField);
        addField(fields, constraints, row++, "Действующая скидка", discountField);

        imagePreview.setHorizontalAlignment(JLabel.CENTER);
        imagePreview.setPreferredSize(new Dimension(300, 200));
        imagePreview.setBorder(Ui.panelBorder());
        JButton chooseImage = Ui.button("Выбрать изображение");
        chooseImage.addActionListener(event -> chooseImage());
        JPanel imagePanel = new JPanel(new BorderLayout(8, 8));
        imagePanel.setOpaque(false);
        imagePanel.add(imagePreview, BorderLayout.CENTER);
        imagePanel.add(chooseImage, BorderLayout.SOUTH);
        addField(fields, constraints, row, "Фото товара", imagePanel);
        root.add(new JScrollPane(fields), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        JButton cancel = Ui.button("Назад");
        cancel.addActionListener(event -> dispose());
        JButton save = Ui.button("Сохранить");
        save.addActionListener(event -> save());
        buttons.add(cancel);
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
            fillCombo(categoryBox, database.listReferences("categories"));
            fillCombo(manufacturerBox, database.listReferences("manufacturers"));
            fillCombo(supplierBox, database.listReferences("suppliers"));
            fillCombo(unitBox, database.listReferences("units"));
            if (productId == null) {
                imagePath = null;
                updatePreview();
                return;
            }
            Product product = database.getProduct(productId);
            if (product == null) {
                Ui.showError(this, "Товар не найден.");
                dispose();
                return;
            }
            idField.setText(String.valueOf(product.id()));
            articleField.setText(product.article());
            nameField.setText(product.name());
            select(categoryBox, product.category().id());
            descriptionArea.setText(product.description());
            select(manufacturerBox, product.manufacturer().id());
            select(supplierBox, product.supplier().id());
            priceField.setText(product.price().toPlainString());
            select(unitBox, product.unit().id());
            stockField.setText(String.valueOf(product.stock()));
            discountField.setText(String.valueOf(product.discount()));
            imagePath = product.imagePath();
            originalImagePath = product.imagePath();
            updatePreview();
        } catch (Exception error) {
            Ui.showError(this, error);
            dispose();
        }
    }

    private void fillCombo(JComboBox<ReferenceItem> comboBox, List<ReferenceItem> items) {
        comboBox.removeAllItems();
        for (ReferenceItem item : items) {
            comboBox.addItem(item);
        }
    }

    private void select(JComboBox<ReferenceItem> comboBox, long id) {
        for (int index = 0; index < comboBox.getItemCount(); index++) {
            if (comboBox.getItemAt(index).id() == id) {
                comboBox.setSelectedIndex(index);
                return;
            }
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выбор изображения товара");
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Изображения JPG, JPEG, PNG",
                "jpg",
                "jpeg",
                "png"
        ));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImage = chooser.getSelectedFile().toPath();
            imagePreview.setIcon(imageService.loadIcon(selectedImage.toString(), 300, 200));
        }
    }

    private void updatePreview() {
        imagePreview.setIcon(imageService.loadIcon(imagePath, 300, 200));
    }

    private void save() {
        String newImagePath = imagePath;
        try {
            if (selectedImage != null) {
                newImagePath = imageService.saveProductImage(selectedImage);
            }
            ProductInput input = database.validateProductInput(
                    articleField.getText(),
                    nameField.getText(),
                    (ReferenceItem) unitBox.getSelectedItem(),
                    priceField.getText(),
                    (ReferenceItem) supplierBox.getSelectedItem(),
                    (ReferenceItem) manufacturerBox.getSelectedItem(),
                    (ReferenceItem) categoryBox.getSelectedItem(),
                    discountField.getText(),
                    stockField.getText(),
                    descriptionArea.getText(),
                    newImagePath
            );
            if (productId == null) {
                database.createProduct(input);
            } else {
                database.updateProduct(productId, input);
            }
            if (selectedImage != null && originalImagePath != null) {
                imageService.deleteReplacedImage(originalImagePath);
            }
            imagePath = newImagePath;
            saved = true;
            dispose();
        } catch (Exception error) {
            if (selectedImage != null && newImagePath != null && !newImagePath.equals(imagePath)) {
                imageService.deleteReplacedImage(newImagePath);
            }
            Ui.showError(this, error);
        }
    }
}

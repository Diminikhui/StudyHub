package ru.mirigrushek.ui;

import ru.mirigrushek.model.Product;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.ImageService;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Locale;

public final class ProductCard extends JPanel {
    private Runnable editAction;
    private Runnable deleteAction;

    public ProductCard(Product product, ImageService imageService, boolean editable) {
        super(new BorderLayout(12, 8));
        Color background = product.stock() == 0
                ? AppColors.OUT_OF_STOCK
                : product.discount() > 17
                ? AppColors.DISCOUNT
                : AppColors.PRIMARY_BACKGROUND;
        setBackground(background);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        setPreferredSize(new Dimension(1100, 210));

        JLabel image = new JLabel(imageService.loadIcon(product.imagePath(), 190, 130));
        image.setPreferredSize(new Dimension(200, 170));
        add(image, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);
        JLabel heading = new JLabel(html(product.category().name() + " | " + product.name(), 640));
        Ui.titleFont(heading, 16);
        details.add(heading);
        details.add(Box.createVerticalStrut(3));
        details.add(new JLabel(html("Описание: " + product.description(), 640)));
        details.add(Box.createVerticalStrut(3));
        details.add(new JLabel("Производитель: " + product.manufacturer().name()));
        details.add(new JLabel("Поставщик: " + product.supplier().name()));
        details.add(new JLabel(priceHtml(product)));
        details.add(new JLabel("Единица измерения: " + product.unit().name()));
        details.add(new JLabel("Количество на складе: " + product.stock()));
        add(details, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(205, 180));
        JLabel discount = new JLabel(
                "<html><div style='text-align:center'>Действующая скидка<br><b>"
                        + product.discount() + "%</b></div></html>"
        );
        discount.setHorizontalAlignment(JLabel.CENTER);
        discount.setPreferredSize(new Dimension(190, 70));
        right.add(discount, BorderLayout.NORTH);
        if (editable) {
            JPanel buttons = new JPanel(new GridLayout(2, 1, 0, 8));
            buttons.setOpaque(false);
            buttons.setPreferredSize(new Dimension(190, 90));
            JButton edit = Ui.button("Изменить");
            JButton delete = Ui.button("Удалить");
            edit.addActionListener(event -> run(editAction));
            delete.addActionListener(event -> run(deleteAction));
            buttons.add(edit);
            buttons.add(delete);
            right.add(buttons, BorderLayout.SOUTH);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        run(editAction);
                    }
                }
            });
        }
        add(right, BorderLayout.EAST);
    }

    public void setEditAction(Runnable editAction) {
        this.editAction = editAction;
    }

    public void setDeleteAction(Runnable deleteAction) {
        this.deleteAction = deleteAction;
    }

    private String priceHtml(Product product) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        if (product.discount() > 0) {
            return "<html>Цена: <font color='red'><strike>"
                    + format.format(product.price())
                    + " руб.</strike></font> <font color='black'><b>"
                    + format.format(product.finalPrice())
                    + " руб.</b></font></html>";
        }
        return "Цена: " + format.format(product.price()) + " руб.";
    }

    private String html(String text, int width) {
        String value = text == null ? "" : text;
        if (value.length() > 230) {
            value = value.substring(0, 227) + "...";
        }
        return "<html><div style='width:" + width + "px'>" + escape(value) + "</div></html>";
    }

    private String escape(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private void run(Runnable action) {
        if (action != null) {
            action.run();
        }
    }
}

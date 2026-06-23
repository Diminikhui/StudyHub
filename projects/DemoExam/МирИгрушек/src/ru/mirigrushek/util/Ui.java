package ru.mirigrushek.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.sql.SQLException;

public final class Ui {
    private static final Font FONT = new Font("Arial", Font.PLAIN, 14);

    private Ui() {
    }

    public static void installDefaults() {
        UIManager.put("Label.font", FONT);
        UIManager.put("Button.font", FONT);
        UIManager.put("TextField.font", FONT);
        UIManager.put("PasswordField.font", FONT);
        UIManager.put("TextArea.font", FONT);
        UIManager.put("ComboBox.font", FONT);
        UIManager.put("Table.font", FONT);
        UIManager.put("TableHeader.font", FONT.deriveFont(Font.BOLD));
        UIManager.put("TabbedPane.font", FONT);
        UIManager.put("OptionPane.messageFont", FONT);
        UIManager.put("OptionPane.buttonFont", FONT);
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setBackground(AppColors.ACCENT);
        button.setFocusPainted(false);
        button.setMargin(new Insets(8, 16, 8, 16));
        return button;
    }

    public static Border panelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }

    public static void titleFont(JComponent component, float size) {
        component.setFont(new Font("Arial", Font.BOLD, Math.round(size)));
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Ошибка",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showError(Component parent, Exception error) {
        String message = error.getMessage();
        if (error instanceof SQLException) {
            message = databaseMessage((SQLException) error);
        }
        if (message == null || message.isBlank()) {
            message = "Операция не выполнена.";
        }
        showError(parent, message);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Информация",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(
                parent,
                message,
                "Предупреждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    private static String databaseMessage(SQLException error) {
        String state = error.getSQLState();
        if ("23505".equals(state)) {
            return "Запись с таким уникальным значением уже существует.";
        }
        if ("23503".equals(state)) {
            return "Операция запрещена, потому что запись используется в связанных данных.";
        }
        if ("23514".equals(state)) {
            return "Введённые данные нарушают ограничения базы данных.";
        }
        if ("08001".equals(state) || "08006".equals(state) || "28P01".equals(state)) {
            return "Не удалось подключиться к PostgreSQL. Проверьте сервер и resources/db.properties.";
        }
        return error.getMessage();
    }
}

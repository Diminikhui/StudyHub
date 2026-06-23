package ru.mirigrushek.ui;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.UserSession;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.ImageService;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public final class LoginPanel extends JPanel {
    private final Database database;
    private final Consumer<UserSession> onLogin;
    private final JTextField loginField;
    private final JPasswordField passwordField;

    public LoginPanel(Database database, ImageService imageService, Consumer<UserSession> onLogin) {
        super(new GridBagLayout());
        this.database = database;
        this.onLogin = onLogin;
        setBackground(AppColors.PRIMARY_BACKGROUND);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(AppColors.SECONDARY_BACKGROUND);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER),
                BorderFactory.createEmptyBorder(24, 36, 28, 36)
        ));

        JLabel logo = new JLabel(imageService.loadIcon("resources/images/icon.png", 150, 150));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(logo);

        JLabel title = new JLabel("Вход в систему");
        Ui.titleFont(title, 24);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(Box.createVerticalStrut(8));
        form.add(title);
        form.add(Box.createVerticalStrut(22));

        JLabel loginLabel = new JLabel("Логин");
        loginLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginLabel);
        loginField = new JTextField(24);
        loginField.setMaximumSize(loginField.getPreferredSize());
        loginField.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginField);
        form.add(Box.createVerticalStrut(12));

        JLabel passwordLabel = new JLabel("Пароль");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(passwordLabel);
        passwordField = new JPasswordField(24);
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addActionListener(this::login);
        form.add(passwordField);
        form.add(Box.createVerticalStrut(18));

        JButton loginButton = Ui.button("Войти");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(this::login);
        form.add(loginButton);
        form.add(Box.createVerticalStrut(10));

        JButton guestButton = Ui.button("Просмотреть товары как гость");
        guestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guestButton.addActionListener(event -> openGuest());
        form.add(guestButton);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(20, 20, 20, 20);
        add(form, constraints);
    }

    private void login(ActionEvent event) {
        String login = loginField.getText().strip();
        String password = new String(passwordField.getPassword());
        if (login.isEmpty() || password.isEmpty()) {
            Ui.showError(this, "Введите логин и пароль.");
            return;
        }
        try {
            UserSession user = database.authenticate(login, password);
            if (user == null) {
                Ui.showError(this, "Неверный логин или пароль.");
                return;
            }
            onLogin.accept(user);
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }

    private void openGuest() {
        try {
            database.checkConnection();
            onLogin.accept(UserSession.guest());
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }
}

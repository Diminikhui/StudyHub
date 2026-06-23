package ru.mirigrushek.ui;

import ru.mirigrushek.app.AppConfig;
import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.UserSession;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.ImageService;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public final class MainFrame extends JFrame {
    private final AppConfig config;
    private final Database database;
    private final ImageService imageService;

    public MainFrame(AppConfig config, Database database, ImageService imageService) {
        this.config = config;
        this.database = database;
        this.imageService = imageService;
        setTitle("ООО «МирИгрушек» — вход");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        if (config.iconPath().toFile().exists()) {
            setIconImage(imageService.loadIcon(config.iconPath().toString(), 64, 64).getImage());
        }
        showLogin();
    }

    public void showLogin() {
        setTitle("ООО «МирИгрушек» — вход");
        setContentPane(new LoginPanel(database, imageService, this::showWorkspace));
        revalidate();
        repaint();
    }

    private void showWorkspace(UserSession session) {
        setTitle("ООО «МирИгрушек» — каталог товаров");
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppColors.PRIMARY_BACKGROUND);
        root.add(createHeader(session), BorderLayout.NORTH);
        ProductPanel productPanel = new ProductPanel(database, imageService, session);
        if (session.canManageCatalogView()) {
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Товары", productPanel);
            tabs.addTab("Заказы", new OrderPanel(database, session));
            root.add(tabs, BorderLayout.CENTER);
        } else {
            root.add(productPanel, BorderLayout.CENTER);
        }
        setContentPane(root);
        revalidate();
        repaint();
    }

    private JPanel createHeader(UserSession session) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(AppColors.SECONDARY_BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel logo = new JLabel(imageService.loadIcon(config.iconPath().toString(), 54, 54));
        JLabel title = new JLabel("ООО «МирИгрушек»");
        Ui.titleFont(title, 22);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(logo);
        left.add(title);
        header.add(left, BorderLayout.WEST);

        JLabel user = new JLabel(session.fullName());
        Ui.titleFont(user, 15);
        JButton logout = Ui.button("Выйти");
        logout.addActionListener(event -> showLogin());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setOpaque(false);
        right.add(user);
        right.add(logout);
        header.add(right, BorderLayout.EAST);
        return header;
    }
}

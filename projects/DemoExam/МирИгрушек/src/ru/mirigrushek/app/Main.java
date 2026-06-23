package ru.mirigrushek.app;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.ui.MainFrame;
import ru.mirigrushek.util.ImageService;
import ru.mirigrushek.util.Ui;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Ui.installDefaults();
        SwingUtilities.invokeLater(() -> {
            AppConfig config = AppConfig.load();
            Database database = new Database(config);
            ImageService imageService = new ImageService(config);
            MainFrame frame = new MainFrame(config, database, imageService);
            frame.setVisible(true);
        });
    }
}

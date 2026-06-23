package ru.mirigrushek.util;

import ru.mirigrushek.app.AppConfig;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ImageService {
    private final AppConfig config;

    public ImageService(AppConfig config) {
        this.config = config;
    }

    public ImageIcon loadIcon(String imagePath, int width, int height) {
        Path path = imagePath == null || imagePath.isBlank()
                ? config.placeholderPath()
                : Path.of(imagePath);
        if (!Files.exists(path)) {
            path = config.placeholderPath();
        }
        try {
            BufferedImage source = ImageIO.read(path.toFile());
            if (source == null) {
                source = ImageIO.read(config.placeholderPath().toFile());
            }
            Image scaled = source.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException error) {
            return new ImageIcon();
        }
    }

    public String saveProductImage(Path source) throws IOException {
        Files.createDirectories(config.imageDirectory());
        BufferedImage original = ImageIO.read(source.toFile());
        if (original == null) {
            throw new IOException("Выбранный файл не является поддерживаемым изображением.");
        }
        BufferedImage result = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 300, 200);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scale = Math.min(300.0 / original.getWidth(), 200.0 / original.getHeight());
        int width = Math.max(1, (int) Math.round(original.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(original.getHeight() * scale));
        int x = (300 - width) / 2;
        int y = (200 - height) / 2;
        graphics.drawImage(original, x, y, width, height, null);
        graphics.dispose();
        String name = "product_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")) + ".jpg";
        Path destination = config.imageDirectory().resolve(name);
        ImageIO.write(result, "jpg", destination.toFile());
        return destination.toString().replace('\\', '/');
    }

    public void deleteReplacedImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }
        Path path = Path.of(imagePath);
        if (!path.normalize().startsWith(config.imageDirectory().normalize())) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    public void copyIconIfNeeded(Path destination) throws IOException {
        Files.copy(config.iconPath(), destination, StandardCopyOption.REPLACE_EXISTING);
    }
}

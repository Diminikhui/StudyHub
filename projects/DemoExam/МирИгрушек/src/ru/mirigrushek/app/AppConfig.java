package ru.mirigrushek.app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AppConfig {
    private final Properties values;

    private AppConfig(Properties values) {
        this.values = values;
    }

    public static AppConfig load() {
        Path path = Path.of("resources", "db.properties");
        Properties values = new Properties();
        if (Files.exists(path)) {
            try (InputStream input = Files.newInputStream(path)) {
                values.load(input);
            } catch (IOException error) {
                throw new IllegalStateException("Не удалось прочитать resources/db.properties.", error);
            }
        }
        return new AppConfig(values);
    }

    public String databaseUrl() {
        return value("TOY_STORE_DB_URL", "db.url", "jdbc:postgresql://localhost:5432/toy_store");
    }

    public String databaseUser() {
        return value("TOY_STORE_DB_USER", "db.user", "postgres");
    }

    public String databasePassword() {
        return value("TOY_STORE_DB_PASSWORD", "db.password", "postgres");
    }

    public Path imageDirectory() {
        return Path.of(values.getProperty("images.directory", "resources/images/products"));
    }

    public Path placeholderPath() {
        return Path.of(values.getProperty("images.placeholder", "resources/images/picture.png"));
    }

    public Path iconPath() {
        return Path.of(values.getProperty("images.icon", "resources/images/icon.png"));
    }

    private String value(String environmentName, String propertyName, String fallback) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return values.getProperty(propertyName, fallback);
    }
}

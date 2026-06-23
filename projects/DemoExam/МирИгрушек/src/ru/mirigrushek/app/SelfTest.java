package ru.mirigrushek.app;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.ProductInput;
import ru.mirigrushek.model.ReferenceItem;

public final class SelfTest {
    private SelfTest() {
    }

    public static void main(String[] args) {
        Database database = new Database(AppConfig.load());
        ReferenceItem item = new ReferenceItem(1, "Значение");
        ProductInput product = database.validateProductInput(
                "test01",
                "Тестовый товар",
                item,
                "125,50",
                item,
                item,
                item,
                "17",
                "0",
                "Описание",
                null
        );
        require("TEST01".equals(product.article()), "Артикул должен преобразовываться в верхний регистр.");
        require(product.price().toPlainString().equals("125.50"), "Цена должна сохранять сотые части.");
        require(product.stock() == 0, "Нулевой остаток должен быть допустим.");
        expectFailure(
                () -> database.validateProductInput(
                        "A",
                        "B",
                        item,
                        "-1",
                        item,
                        item,
                        item,
                        "0",
                        "1",
                        "",
                        null
                ),
                "Отрицательная цена должна отклоняться."
        );
        expectFailure(
                () -> database.validateProductInput(
                        "A",
                        "B",
                        item,
                        "1",
                        item,
                        item,
                        item,
                        "101",
                        "1",
                        "",
                        null
                ),
                "Скидка больше 100 должна отклоняться."
        );
        System.out.println("Внутренние проверки Java завершены успешно.");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static void expectFailure(Runnable action, String message) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new IllegalStateException(message);
    }
}

package ru.mirigrushek.model;

import java.math.BigDecimal;

public record ProductInput(
        String article,
        String name,
        long unitId,
        BigDecimal price,
        long supplierId,
        long manufacturerId,
        long categoryId,
        int discount,
        int stock,
        String description,
        String imagePath
) {
}

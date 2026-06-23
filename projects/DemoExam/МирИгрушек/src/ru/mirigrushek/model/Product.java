package ru.mirigrushek.model;

import java.math.BigDecimal;

public record Product(
        long id,
        String article,
        String name,
        ReferenceItem unit,
        BigDecimal price,
        ReferenceItem supplier,
        ReferenceItem manufacturer,
        ReferenceItem category,
        int discount,
        int stock,
        String description,
        String imagePath,
        BigDecimal finalPrice
) {
}

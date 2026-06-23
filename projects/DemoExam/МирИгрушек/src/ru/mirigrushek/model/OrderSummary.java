package ru.mirigrushek.model;

public record OrderSummary(
        long id,
        int orderNumber,
        String items,
        String orderDate,
        String deliveryDate,
        String pickupAddress,
        String clientName,
        String pickupCode,
        String status
) {
}

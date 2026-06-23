package ru.mirigrushek.model;

import java.util.List;

public record OrderData(
        long id,
        int orderNumber,
        String orderDate,
        String deliveryDate,
        long pickupPointId,
        long userId,
        String pickupCode,
        String status,
        List<OrderItemInput> items
) {
}

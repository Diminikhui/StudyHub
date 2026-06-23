package ru.mirigrushek.model;

public record OrderItemInput(long productId, String article, int quantity) {
}

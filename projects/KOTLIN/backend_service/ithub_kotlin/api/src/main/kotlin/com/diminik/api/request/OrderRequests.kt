package com.diminik.api.request

import com.diminik.domain.model.CreateOrderCommand
import com.diminik.domain.model.CreateOrderItemCommand
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
) {
    fun toCommand(): CreateOrderItemCommand = CreateOrderItemCommand(
        productId = productId,
        quantity = quantity,
    )
}

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
) {
    fun toCommand(): CreateOrderCommand = CreateOrderCommand(
        items = items.map(OrderItemRequest::toCommand),
    )
}

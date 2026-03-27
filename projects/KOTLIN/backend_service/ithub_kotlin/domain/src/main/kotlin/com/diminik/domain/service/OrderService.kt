package com.diminik.domain.service

import com.diminik.domain.exception.ConflictException
import com.diminik.domain.exception.ForbiddenException
import com.diminik.domain.exception.InsufficientStockException
import com.diminik.domain.exception.NotFoundException
import com.diminik.domain.exception.ValidationException
import com.diminik.domain.model.CreateOrderCommand
import com.diminik.domain.model.Order
import com.diminik.domain.model.OrderEvent
import com.diminik.domain.model.OrderEventType
import com.diminik.domain.model.OrderItem
import com.diminik.domain.model.OrderStats
import com.diminik.domain.model.OrderStatus
import com.diminik.domain.model.Product
import com.diminik.domain.ports.OrderCache
import com.diminik.domain.ports.OrderEventPublisher
import com.diminik.domain.ports.OrderRepository
import com.diminik.domain.ports.ProductCache
import com.diminik.domain.ports.ProductRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.UUID

class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val productCache: ProductCache,
    private val orderCache: OrderCache,
    private val eventPublisher: OrderEventPublisher,
) {
    suspend fun create(userId: String, command: CreateOrderCommand): Order {
        if (userId.isBlank()) {
            throw ValidationException("Не удалось определить пользователя")
        }
        validateCommand(command)

        val requestedItems = command.items
            .groupBy { it.productId }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }

        val productsById = requestedItems.keys.associateWith { productId ->
            productRepository.getById(productId)
                ?: throw NotFoundException("Один или несколько товаров не найдены")
        }

        requestedItems.forEach { (productId, quantity) ->
            val product = productsById.getValue(productId)
            if (product.stock < quantity) {
                throw InsufficientStockException("Недостаточно товара ${product.name} на складе")
            }
        }

        val now = Instant.now().toString()
        val orderItems = requestedItems.map { (productId, quantity) ->
            val product = productsById.getValue(productId)
            OrderItem(
                productId = product.id,
                productName = product.name,
                price = product.price.roundMoney(),
                quantity = quantity,
            )
        }
        val total = orderItems.sumOf { it.price * it.quantity }.roundMoney()
        val order = orderRepository.create(
            order = Order(
                id = UUID.randomUUID().toString(),
                userId = userId,
                status = OrderStatus.CREATED,
                total = total,
                items = orderItems,
                createdAt = now,
                updatedAt = now,
            ),
            auditAction = "ORDER_CREATED",
            auditDetails = "Создан заказ на ${orderItems.size} товаров",
        )

        orderCache.put(order)
        invalidateProductCache(orderItems)
        eventPublisher.publish(
            OrderEvent(
                id = UUID.randomUUID().toString(),
                type = OrderEventType.ORDER_CREATED,
                orderId = order.id,
                userId = userId,
                createdAt = order.createdAt,
                message = "Создан заказ ${order.id} на сумму ${order.total}",
            ),
        )
        return order
    }

    suspend fun getHistory(userId: String): List<Order> {
        if (userId.isBlank()) {
            throw ValidationException("Не удалось определить пользователя")
        }
        return orderRepository.listByUser(userId).also { orders ->
            orders.forEach { orderCache.put(it) }
        }
    }

    suspend fun cancel(orderId: String, userId: String): Order {
        if (orderId.isBlank()) {
            throw ValidationException("Нужно указать id заказа")
        }
        if (userId.isBlank()) {
            throw ValidationException("Не удалось определить пользователя")
        }

        val existingOrder = orderCache.get(orderId)
            ?: orderRepository.findById(orderId)?.also { orderCache.put(it) }
            ?: throw NotFoundException("Заказ не найден")

        if (existingOrder.userId != userId) {
            throw ForbiddenException("Нельзя отменить чужой заказ")
        }
        if (existingOrder.status == OrderStatus.CANCELLED) {
            throw ConflictException("Заказ уже отменен")
        }

        val order = orderRepository.cancel(
            order = existingOrder.copy(
                status = OrderStatus.CANCELLED,
                updatedAt = Instant.now().toString(),
            ),
            auditAction = "ORDER_CANCELLED",
            auditDetails = "Заказ отменен пользователем",
        )

        orderCache.invalidate(orderId)
        orderCache.put(order)
        invalidateProductCache(order.items)
        eventPublisher.publish(
            OrderEvent(
                id = UUID.randomUUID().toString(),
                type = OrderEventType.ORDER_CANCELLED,
                orderId = order.id,
                userId = userId,
                createdAt = order.updatedAt,
                message = "Заказ ${order.id} был отменен",
            ),
        )
        return order
    }

    suspend fun getStats(): OrderStats = orderRepository.getStats()

    private suspend fun invalidateProductCache(items: List<OrderItem>) {
        items.map { it.productId }
            .distinct()
            .forEach { productCache.invalidate(it) }
    }

    private fun validateCommand(command: CreateOrderCommand) {
        if (command.items.isEmpty()) {
            throw ValidationException("Заказ должен содержать хотя бы один товар")
        }
        command.items.forEach { item ->
            if (item.productId.isBlank()) {
                throw ValidationException("У каждого товара в заказе должен быть productId")
            }
            if (item.quantity <= 0) {
                throw ValidationException("Количество товара должно быть больше нуля")
            }
        }
    }

    private fun Double.roundMoney(): Double = BigDecimal.valueOf(this)
        .setScale(2, RoundingMode.HALF_UP)
        .toDouble()
}

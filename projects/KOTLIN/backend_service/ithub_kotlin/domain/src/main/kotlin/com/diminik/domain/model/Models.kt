package com.diminik.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    USER,
    ADMIN,
}

@Serializable
enum class OrderStatus {
    CREATED,
    CANCELLED,
}

@Serializable
enum class OrderEventType {
    ORDER_CREATED,
    ORDER_CANCELLED,
}

@Serializable
data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val createdAt: String,
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User,
)

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class OrderItem(
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
)

@Serializable
data class Order(
    val id: String,
    val userId: String,
    val status: OrderStatus,
    val total: Double,
    val items: List<OrderItem>,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class OrderStats(
    val totalOrders: Long,
    val createdOrders: Long,
    val cancelledOrders: Long,
    val totalRevenue: Double,
    val uniqueCustomers: Long,
)

@Serializable
data class OrderEvent(
    val id: String,
    val type: OrderEventType,
    val orderId: String,
    val userId: String,
    val createdAt: String,
    val message: String,
)

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String,
)

data class RegisterCommand(
    val email: String,
    val password: String,
    val fullName: String,
)

data class LoginCommand(
    val email: String,
    val password: String,
)

data class CreateProductCommand(
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
)

data class UpdateProductCommand(
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
)

data class CreateOrderItemCommand(
    val productId: String,
    val quantity: Int,
)

data class CreateOrderCommand(
    val items: List<CreateOrderItemCommand>,
)

data class UserCredentials(
    val user: User,
    val passwordHash: String,
)

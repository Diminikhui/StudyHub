package com.diminik.domain.ports

import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.LoginCommand
import com.diminik.domain.model.Order
import com.diminik.domain.model.OrderEvent
import com.diminik.domain.model.OrderStats
import com.diminik.domain.model.Product
import com.diminik.domain.model.RegisterCommand
import com.diminik.domain.model.UpdateProductCommand
import com.diminik.domain.model.User
import com.diminik.domain.model.UserCredentials

interface UserRepository {
    suspend fun create(command: RegisterCommand, passwordHash: String): User
    suspend fun findCredentialsByEmail(email: String): UserCredentials?
    suspend fun findById(id: String): User?
    suspend fun existsByEmail(email: String): Boolean
    suspend fun ensureAdminAccount(command: RegisterCommand, passwordHash: String): User
}

interface ProductRepository {
    suspend fun getAll(): List<Product>
    suspend fun getById(id: String): Product?
    suspend fun create(command: CreateProductCommand): Product
    suspend fun update(id: String, command: UpdateProductCommand): Product
    suspend fun delete(id: String): Boolean
}

interface OrderRepository {
    suspend fun create(order: Order, auditAction: String, auditDetails: String): Order
    suspend fun findById(orderId: String): Order?
    suspend fun listByUser(userId: String): List<Order>
    suspend fun cancel(order: Order, auditAction: String, auditDetails: String): Order
    suspend fun getStats(): OrderStats
}

interface ProductCache {
    suspend fun get(productId: String): Product?
    suspend fun put(product: Product)
    suspend fun invalidate(productId: String)
}

interface OrderCache {
    suspend fun get(orderId: String): Order?
    suspend fun put(order: Order)
    suspend fun invalidate(orderId: String)
}

interface PasswordHasher {
    fun hash(rawPassword: String): String
    fun matches(rawPassword: String, passwordHash: String): Boolean
}

interface TokenIssuer {
    fun issue(user: User): String
}

interface OrderEventPublisher {
    suspend fun publish(event: OrderEvent)
}

interface EmailSender {
    suspend fun sendOrderNotification(event: OrderEvent)
}

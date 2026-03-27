package com.diminik.api

import com.diminik.api.di.AppComponents
import com.diminik.core.config.SecuritySettings
import com.diminik.core.security.BcryptPasswordHasher
import com.diminik.core.security.JwtTokenIssuer
import com.diminik.domain.exception.ConflictException
import com.diminik.domain.exception.ForbiddenException
import com.diminik.domain.exception.NotFoundException
import com.diminik.domain.model.CreateOrderCommand
import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.Order
import com.diminik.domain.model.OrderStats
import com.diminik.domain.model.OrderStatus
import com.diminik.domain.model.Product
import com.diminik.domain.model.RegisterCommand
import com.diminik.domain.model.UpdateProductCommand
import com.diminik.domain.model.User
import com.diminik.domain.model.UserCredentials
import com.diminik.domain.model.UserRole
import com.diminik.domain.ports.OrderCache
import com.diminik.domain.ports.OrderEventPublisher
import com.diminik.domain.ports.OrderRepository
import com.diminik.domain.ports.ProductCache
import com.diminik.domain.ports.ProductRepository
import com.diminik.domain.ports.UserRepository
import com.diminik.domain.service.AuthService
import com.diminik.domain.service.OrderService
import com.diminik.domain.service.ProductService
import java.time.Instant
import java.util.UUID

object TestAppFactory {
    fun createComponents(): AppComponents {
        val settings = SecuritySettings(
            issuer = "test-issuer",
            audience = "test-audience",
            realm = "test-realm",
            secret = "test-secret",
            tokenTtlMinutes = 120,
            bootstrapAdminEmail = "admin@test.local",
            bootstrapAdminPassword = "admin123",
            bootstrapAdminName = "Test Admin",
        )
        val store = TestStore()
        val userRepository = InMemoryUserRepository(store)
        val productRepository = InMemoryProductRepository(store)
        val orderRepository = InMemoryOrderRepository(store)
        val productCache = InMemoryProductCache()
        val orderCache = InMemoryOrderCache()
        val passwordHasher = BcryptPasswordHasher()
        val jwtTokenIssuer = JwtTokenIssuer(settings)

        seedAdmin(store, passwordHasher.hash(settings.bootstrapAdminPassword), settings)

        return AppComponents(
            authService = AuthService(userRepository, passwordHasher, jwtTokenIssuer),
            productService = ProductService(productRepository, productCache),
            orderService = OrderService(
                orderRepository = orderRepository,
                productRepository = productRepository,
                productCache = productCache,
                orderCache = orderCache,
                eventPublisher = NoopEventPublisher(),
            ),
            jwtTokenIssuer = jwtTokenIssuer,
            securitySettings = settings,
            close = {},
        )
    }

    private fun seedAdmin(store: TestStore, passwordHash: String, settings: SecuritySettings) {
        val user = User(
            id = UUID.randomUUID().toString(),
            email = settings.bootstrapAdminEmail,
            fullName = settings.bootstrapAdminName,
            role = UserRole.ADMIN,
            createdAt = Instant.now().toString(),
        )
        store.users[user.email] = UserCredentials(user, passwordHash)
    }
}

private data class TestStore(
    val users: MutableMap<String, UserCredentials> = linkedMapOf(),
    val products: MutableMap<String, Product> = linkedMapOf(),
    val orders: MutableMap<String, Order> = linkedMapOf(),
)

private class InMemoryUserRepository(
    private val store: TestStore,
) : UserRepository {
    override suspend fun create(command: RegisterCommand, passwordHash: String): User {
        if (store.users.containsKey(command.email)) {
            throw ConflictException("Пользователь с таким email уже существует")
        }

        val user = User(
            id = UUID.randomUUID().toString(),
            email = command.email,
            fullName = command.fullName,
            role = UserRole.USER,
            createdAt = Instant.now().toString(),
        )
        store.users[user.email] = UserCredentials(user, passwordHash)
        return user
    }

    override suspend fun findCredentialsByEmail(email: String): UserCredentials? = store.users[email]

    override suspend fun findById(id: String): User? = store.users.values.firstOrNull { it.user.id == id }?.user

    override suspend fun existsByEmail(email: String): Boolean = store.users.containsKey(email)

    override suspend fun ensureAdminAccount(command: RegisterCommand, passwordHash: String): User {
        val existing = store.users[command.email]
        if (existing != null) {
            val updated = existing.user.copy(
                fullName = command.fullName,
                role = UserRole.ADMIN,
            )
            store.users[command.email] = UserCredentials(updated, passwordHash)
            return updated
        }

        val admin = User(
            id = UUID.randomUUID().toString(),
            email = command.email,
            fullName = command.fullName,
            role = UserRole.ADMIN,
            createdAt = Instant.now().toString(),
        )
        store.users[admin.email] = UserCredentials(admin, passwordHash)
        return admin
    }
}

private class InMemoryProductRepository(
    private val store: TestStore,
) : ProductRepository {
    override suspend fun getAll(): List<Product> = store.products.values.toList()

    override suspend fun getById(id: String): Product? = store.products[id]

    override suspend fun create(command: CreateProductCommand): Product {
        val now = Instant.now().toString()
        val product = Product(
            id = UUID.randomUUID().toString(),
            name = command.name,
            description = command.description,
            price = command.price,
            stock = command.stock,
            createdAt = now,
            updatedAt = now,
        )
        store.products[product.id] = product
        return product
    }

    override suspend fun update(id: String, command: UpdateProductCommand): Product {
        val current = store.products[id] ?: throw NotFoundException("Товар не найден")
        val updated = current.copy(
            name = command.name,
            description = command.description,
            price = command.price,
            stock = command.stock,
            updatedAt = Instant.now().toString(),
        )
        store.products[id] = updated
        return updated
    }

    override suspend fun delete(id: String): Boolean = store.products.remove(id) != null
}

private class InMemoryOrderRepository(
    private val store: TestStore,
) : OrderRepository {
    override suspend fun create(order: Order, auditAction: String, auditDetails: String): Order {
        order.items.forEach { item ->
            val product = store.products[item.productId] ?: throw NotFoundException("Товар не найден")
            store.products[item.productId] = product.copy(
                stock = product.stock - item.quantity,
                updatedAt = order.updatedAt,
            )
        }
        store.orders[order.id] = order
        return order
    }

    override suspend fun findById(orderId: String): Order? = store.orders[orderId]

    override suspend fun listByUser(userId: String): List<Order> = store.orders.values.filter { it.userId == userId }

    override suspend fun cancel(order: Order, auditAction: String, auditDetails: String): Order {
        order.items.forEach { item ->
            val product = store.products[item.productId] ?: return@forEach
            store.products[item.productId] = product.copy(
                stock = product.stock + item.quantity,
                updatedAt = order.updatedAt,
            )
        }
        store.orders[order.id] = order
        return order
    }

    override suspend fun getStats(): OrderStats = OrderStats(
        totalOrders = store.orders.size.toLong(),
        createdOrders = store.orders.values.count { it.status == OrderStatus.CREATED }.toLong(),
        cancelledOrders = store.orders.values.count { it.status == OrderStatus.CANCELLED }.toLong(),
        totalRevenue = store.orders.values
            .filter { it.status == OrderStatus.CREATED }
            .sumOf { it.total },
        uniqueCustomers = store.orders.values.map { it.userId }.toSet().size.toLong(),
    )
}

private class InMemoryProductCache : ProductCache {
    private val cache = linkedMapOf<String, Product>()

    override suspend fun get(productId: String): Product? = cache[productId]

    override suspend fun put(product: Product) {
        cache[product.id] = product
    }

    override suspend fun invalidate(productId: String) {
        cache.remove(productId)
    }
}

private class InMemoryOrderCache : OrderCache {
    private val cache = linkedMapOf<String, Order>()

    override suspend fun get(orderId: String): Order? = cache[orderId]

    override suspend fun put(order: Order) {
        cache[order.id] = order
    }

    override suspend fun invalidate(orderId: String) {
        cache.remove(orderId)
    }
}

private class NoopEventPublisher : OrderEventPublisher {
    override suspend fun publish(event: com.diminik.domain.model.OrderEvent) = Unit
}

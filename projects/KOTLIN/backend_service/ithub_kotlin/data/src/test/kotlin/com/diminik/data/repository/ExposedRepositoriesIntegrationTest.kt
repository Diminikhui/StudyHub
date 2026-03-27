package com.diminik.data.repository

import com.diminik.core.config.DatabaseSettings
import com.diminik.data.db.AuditLogsTable
import com.diminik.data.db.DatabaseFactory
import com.diminik.data.db.MigrationRunner
import com.diminik.data.db.OrderItemsTable
import com.diminik.data.db.OrdersTable
import com.diminik.data.db.ProductsTable
import com.diminik.data.db.UsersTable
import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.Order
import com.diminik.domain.model.OrderItem
import com.diminik.domain.model.OrderStatus
import com.diminik.domain.model.RegisterCommand
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.jetbrains.exposed.sql.deleteAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers(disabledWithoutDocker = true)
class ExposedRepositoriesIntegrationTest {
    private val settings = DatabaseSettings(
        jdbcUrl = postgres.jdbcUrl,
        username = postgres.username,
        password = postgres.password,
        maximumPoolSize = 4,
    )

    @Test
    fun `user repository stores and loads credentials`() = runBlocking {
        MigrationRunner.migrate(settings)
        DatabaseFactory(settings).use { databaseFactory ->
            clearTables(databaseFactory)
            val userRepository = ExposedUserRepository(databaseFactory)

            val created = userRepository.create(
                RegisterCommand(
                    email = "integration-user@example.com",
                    password = "secret123",
                    fullName = "Integration User",
                ),
                passwordHash = "hashed-secret123",
            )

            val loaded = userRepository.findCredentialsByEmail("integration-user@example.com")

            assertEquals(created.email, loaded?.user?.email)
            assertEquals("hashed-secret123", loaded?.passwordHash)
        }
    }

    @Test
    fun `order repository decreases and restores stock`() = runBlocking {
        MigrationRunner.migrate(settings)
        DatabaseFactory(settings).use { databaseFactory ->
            clearTables(databaseFactory)
            val userRepository = ExposedUserRepository(databaseFactory)
            val productRepository = ExposedProductRepository(databaseFactory)
            val orderRepository = ExposedOrderRepository(databaseFactory)

            val user = userRepository.create(
                RegisterCommand(
                    email = "buyer@example.com",
                    password = "secret123",
                    fullName = "Buyer User",
                ),
                passwordHash = "hashed-secret123",
            )
            val product = productRepository.create(
                CreateProductCommand(
                    name = "Monitor",
                    description = "27 inch",
                    price = 199.99,
                    stock = 10,
                ),
            )

            val createdOrder = orderRepository.create(
                order = Order(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    status = OrderStatus.CREATED,
                    total = 599.97,
                    items = listOf(
                        OrderItem(
                            productId = product.id,
                            productName = product.name,
                            price = product.price,
                            quantity = 3,
                        ),
                    ),
                    createdAt = Instant.now().toString(),
                    updatedAt = Instant.now().toString(),
                ),
                auditAction = "ORDER_CREATED",
                auditDetails = "Создан заказ на 1 товар",
            )
            val stockAfterCreate = productRepository.getById(product.id)
            val cancelledOrder = orderRepository.cancel(
                order = createdOrder.copy(
                    status = OrderStatus.CANCELLED,
                    updatedAt = Instant.now().toString(),
                ),
                auditAction = "ORDER_CANCELLED",
                auditDetails = "Заказ отменен пользователем",
            )
            val stockAfterCancel = productRepository.getById(product.id)

            assertEquals(7, stockAfterCreate?.stock)
            assertEquals(OrderStatus.CANCELLED, cancelledOrder.status)
            assertEquals(10, stockAfterCancel?.stock)
            assertTrue(orderRepository.getStats().totalOrders >= 1)
            assertNotNull(productRepository.getById(product.id))
        }
    }

    private suspend fun clearTables(databaseFactory: DatabaseFactory) {
        databaseFactory.dbQuery {
            OrderItemsTable.deleteAll()
            OrdersTable.deleteAll()
            AuditLogsTable.deleteAll()
            ProductsTable.deleteAll()
            UsersTable.deleteAll()
        }
    }

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("kotlinithub")
            .withUsername("kotlinithub")
            .withPassword("kotlinithub")
    }
}

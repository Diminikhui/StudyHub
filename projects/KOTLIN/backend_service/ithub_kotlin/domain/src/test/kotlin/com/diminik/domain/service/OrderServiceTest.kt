package com.diminik.domain.service

import com.diminik.domain.exception.ValidationException
import com.diminik.domain.model.CreateOrderCommand
import com.diminik.domain.model.CreateOrderItemCommand
import com.diminik.domain.model.Order
import com.diminik.domain.model.OrderEvent
import com.diminik.domain.model.OrderEventType
import com.diminik.domain.model.OrderStats
import com.diminik.domain.model.OrderStatus
import com.diminik.domain.model.Product
import com.diminik.domain.ports.OrderCache
import com.diminik.domain.ports.OrderEventPublisher
import com.diminik.domain.ports.OrderRepository
import com.diminik.domain.ports.ProductCache
import com.diminik.domain.ports.ProductRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OrderServiceTest {
    @Test
    fun `create calculates total and invalidates product cache`() = runTest {
        val repository = FakeOrderRepository()
        val productRepository = OrderTestProductRepository().apply {
            seed(
                Product(
                    id = "product-1",
                    name = "Mouse",
                    description = "Wireless",
                    price = 25.55,
                    stock = 10,
                    createdAt = "2026-01-01T00:00:00Z",
                    updatedAt = "2026-01-01T00:00:00Z",
                ),
            )
        }
        val productCache = OrderTestProductCache()
        val publisher = FakeOrderPublisher()
        val service = OrderService(
            orderRepository = repository,
            productRepository = productRepository,
            productCache = productCache,
            orderCache = FakeOrderCache(),
            eventPublisher = publisher,
        )

        val order = service.create(
            "user-1",
            CreateOrderCommand(
                items = listOf(
                    CreateOrderItemCommand(productId = "product-1", quantity = 2),
                    CreateOrderItemCommand(productId = "product-1", quantity = 1),
                ),
            ),
        )

        assertEquals(76.65, order.total)
        assertEquals(1, order.items.size)
        assertEquals(3, order.items.first().quantity)
        assertEquals(listOf("product-1"), productCache.invalidatedIds)
        assertEquals(OrderStatus.CREATED, repository.createdOrders.single().status)
        assertEquals(OrderEventType.ORDER_CREATED, publisher.events.single().type)
    }

    @Test
    fun `create rejects empty basket`() = runTest {
        val service = OrderService(
            orderRepository = FakeOrderRepository(),
            productRepository = OrderTestProductRepository(),
            productCache = OrderTestProductCache(),
            orderCache = FakeOrderCache(),
            eventPublisher = FakeOrderPublisher(),
        )

        assertFailsWith<ValidationException> {
            service.create("user-1", CreateOrderCommand(emptyList()))
        }
    }

    @Test
    fun `cancel publishes cancellation event`() = runTest {
        val repository = FakeOrderRepository()
        val publisher = FakeOrderPublisher()
        val service = OrderService(
            orderRepository = repository,
            productRepository = OrderTestProductRepository(),
            productCache = OrderTestProductCache(),
            orderCache = FakeOrderCache(),
            eventPublisher = publisher,
        )
        val order = repository.seedOrder()

        service.cancel(order.id, order.userId)

        assertEquals(1, publisher.events.size)
        assertEquals("Заказ ${order.id} был отменен", publisher.events.first().message)
    }
}

private class FakeOrderRepository : OrderRepository {
    private val orders = linkedMapOf<String, Order>()
    val createdOrders = mutableListOf<Order>()

    override suspend fun create(order: Order, auditAction: String, auditDetails: String): Order {
        createdOrders += order
        orders[order.id] = order
        return order
    }

    override suspend fun findById(orderId: String): Order? = orders[orderId]

    override suspend fun listByUser(userId: String): List<Order> = orders.values.filter { it.userId == userId }

    override suspend fun cancel(order: Order, auditAction: String, auditDetails: String): Order {
        val updatedOrder = orders.getValue(order.id).copy(
            status = OrderStatus.CANCELLED,
            updatedAt = "2026-01-01T02:00:00Z",
        )
        orders[updatedOrder.id] = updatedOrder
        return updatedOrder
    }

    override suspend fun getStats(): OrderStats = OrderStats(0, 0, 0, 0.0, 0)

    fun seedOrder(): Order {
        val order = Order(
            id = "order-1",
            userId = "user-1",
            status = OrderStatus.CREATED,
            total = 99.0,
            items = listOf(),
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z",
        )
        orders[order.id] = order
        return order
    }
}

private class OrderTestProductRepository : ProductRepository {
    private val products = linkedMapOf<String, Product>()

    override suspend fun getAll(): List<Product> = products.values.toList()

    override suspend fun getById(id: String): Product? = products[id]

    override suspend fun create(command: com.diminik.domain.model.CreateProductCommand): Product {
        error("Not required in test")
    }

    override suspend fun update(id: String, command: com.diminik.domain.model.UpdateProductCommand): Product {
        error("Not required in test")
    }

    override suspend fun delete(id: String): Boolean = false

    fun seed(product: Product) {
        products[product.id] = product
    }
}

private class OrderTestProductCache : ProductCache {
    val invalidatedIds = mutableListOf<String>()

    override suspend fun get(productId: String): Product? = null

    override suspend fun put(product: Product) = Unit

    override suspend fun invalidate(productId: String) {
        invalidatedIds += productId
    }
}

private class FakeOrderCache : OrderCache {
    override suspend fun get(orderId: String): Order? = null

    override suspend fun put(order: Order) = Unit

    override suspend fun invalidate(orderId: String) = Unit
}

private class FakeOrderPublisher : OrderEventPublisher {
    val events = mutableListOf<OrderEvent>()

    override suspend fun publish(event: OrderEvent) {
        events += event
    }
}

package com.diminik.data.repository

import com.diminik.data.db.AuditLogsTable
import com.diminik.data.db.DatabaseFactory
import com.diminik.data.db.OrderItemsTable
import com.diminik.data.db.OrdersTable
import com.diminik.data.db.ProductsTable
import com.diminik.data.db.UsersTable
import com.diminik.domain.exception.NotFoundException
import com.diminik.domain.exception.ValidationException
import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.Order
import com.diminik.domain.model.OrderItem
import com.diminik.domain.model.OrderStats
import com.diminik.domain.model.OrderStatus
import com.diminik.domain.model.Product
import com.diminik.domain.model.RegisterCommand
import com.diminik.domain.model.UpdateProductCommand
import com.diminik.domain.model.User
import com.diminik.domain.model.UserCredentials
import com.diminik.domain.model.UserRole
import com.diminik.domain.ports.OrderRepository
import com.diminik.domain.ports.ProductRepository
import com.diminik.domain.ports.UserRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ExposedUserRepository(
    private val databaseFactory: DatabaseFactory,
) : UserRepository {
    override suspend fun create(command: RegisterCommand, passwordHash: String): User = databaseFactory.dbQuery {
        val userId = UUID.randomUUID()
        val createdAt = currentTimestamp()

        UsersTable.insert {
            it[id] = userId
            it[email] = command.email
            it[fullName] = command.fullName
            it[this.passwordHash] = passwordHash
            it[role] = UserRole.USER.name
            it[this.createdAt] = createdAt
        }

        User(
            id = userId.toString(),
            email = command.email,
            fullName = command.fullName,
            role = UserRole.USER,
            createdAt = createdAt,
        )
    }

    override suspend fun findCredentialsByEmail(email: String): UserCredentials? = databaseFactory.dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?.toUserCredentials()
    }

    override suspend fun findById(id: String): User? = databaseFactory.dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.id eq id.toUuid("пользователя") }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun existsByEmail(email: String): Boolean = databaseFactory.dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .limit(1)
            .count() > 0
    }

    override suspend fun ensureAdminAccount(command: RegisterCommand, passwordHash: String): User = databaseFactory.dbQuery {
        val existing = UsersTable.selectAll()
            .where { UsersTable.email eq command.email }
            .singleOrNull()

        if (existing == null) {
            val userId = UUID.randomUUID()
            val createdAt = currentTimestamp()
            UsersTable.insert {
                it[id] = userId
                it[email] = command.email
                it[fullName] = command.fullName
                it[this.passwordHash] = passwordHash
                it[role] = UserRole.ADMIN.name
                it[this.createdAt] = createdAt
            }
        } else {
            UsersTable.update({ UsersTable.email eq command.email }) {
                it[fullName] = command.fullName
                it[this.passwordHash] = passwordHash
                it[role] = UserRole.ADMIN.name
            }
        }

        UsersTable.selectAll()
            .where { UsersTable.email eq command.email }
            .single()
            .toUser()
    }

    private fun ResultRow.toUser(): User = User(
        id = this[UsersTable.id].toString(),
        email = this[UsersTable.email],
        fullName = this[UsersTable.fullName],
        role = UserRole.valueOf(this[UsersTable.role]),
        createdAt = this[UsersTable.createdAt],
    )

    private fun ResultRow.toUserCredentials(): UserCredentials = UserCredentials(
        user = toUser(),
        passwordHash = this[UsersTable.passwordHash],
    )
}

class ExposedProductRepository(
    private val databaseFactory: DatabaseFactory,
) : ProductRepository {
    override suspend fun getAll(): List<Product> = databaseFactory.dbQuery {
        ProductsTable.selectAll()
            .orderBy(ProductsTable.createdAt to SortOrder.DESC)
            .map { it.toProduct() }
    }

    override suspend fun getById(id: String): Product? = databaseFactory.dbQuery {
        ProductsTable.selectAll()
            .where { ProductsTable.id eq id.toUuid("товара") }
            .singleOrNull()
            ?.toProduct()
    }

    override suspend fun create(command: CreateProductCommand): Product = databaseFactory.dbQuery {
        val productId = UUID.randomUUID()
        val createdAt = currentTimestamp()

        ProductsTable.insert {
            it[id] = productId
            it[name] = command.name
            it[description] = command.description
            it[price] = command.price.toMoney()
            it[stock] = command.stock
            it[this.createdAt] = createdAt
            it[updatedAt] = createdAt
        }

        Product(
            id = productId.toString(),
            name = command.name,
            description = command.description,
            price = command.price.roundMoney(),
            stock = command.stock,
            createdAt = createdAt,
            updatedAt = createdAt,
        )
    }

    override suspend fun update(id: String, command: UpdateProductCommand): Product = databaseFactory.dbQuery {
        val productId = id.toUuid("товара")
        val updatedAt = currentTimestamp()
        val updatedRows = ProductsTable.update({ ProductsTable.id eq productId }) {
            it[name] = command.name
            it[description] = command.description
            it[price] = command.price.toMoney()
            it[stock] = command.stock
            it[this.updatedAt] = updatedAt
        }

        if (updatedRows == 0) {
            throw NotFoundException("Товар не найден")
        }

        ProductsTable.selectAll()
            .where { ProductsTable.id eq productId }
            .single()
            .toProduct()
    }

    override suspend fun delete(id: String): Boolean = databaseFactory.dbQuery {
        ProductsTable.deleteWhere { ProductsTable.id eq id.toUuid("товара") } > 0
    }

    private fun ResultRow.toProduct(): Product = Product(
        id = this[ProductsTable.id].toString(),
        name = this[ProductsTable.name],
        description = this[ProductsTable.description],
        price = this[ProductsTable.price].toDouble().roundMoney(),
        stock = this[ProductsTable.stock],
        createdAt = this[ProductsTable.createdAt],
        updatedAt = this[ProductsTable.updatedAt],
    )
}

class ExposedOrderRepository(
    private val databaseFactory: DatabaseFactory,
) : OrderRepository {
    override suspend fun create(order: Order, auditAction: String, auditDetails: String): Order = databaseFactory.dbQuery {
        val orderUuid = order.id.toUuid("заказа")
        val userUuid = order.userId.toUuid("пользователя")

        OrdersTable.insert {
            it[id] = orderUuid
            it[this.userId] = userUuid
            it[status] = order.status.name
            it[this.total] = order.total.toMoney()
            it[createdAt] = order.createdAt
            it[updatedAt] = order.updatedAt
        }

        order.items.forEach { item ->
            val productUuid = item.productId.toUuid("товара")
            val currentProduct = ProductsTable.selectAll()
                .where { ProductsTable.id eq productUuid }
                .singleOrNull()
                ?: throw NotFoundException("Товар не найден")

            ProductsTable.update({ ProductsTable.id eq productUuid }) {
                it[stock] = currentProduct[ProductsTable.stock] - item.quantity
                it[updatedAt] = order.updatedAt
            }

            OrderItemsTable.insert {
                it[id] = UUID.randomUUID()
                it[OrderItemsTable.orderId] = orderUuid
                it[this.productId] = productUuid
                it[this.productName] = item.productName
                it[this.price] = item.price.toMoney()
                it[this.quantity] = item.quantity
            }
        }

        insertAuditLog(
            entityType = "order",
            entityId = order.id,
            action = auditAction,
            userId = userUuid,
            details = auditDetails,
        )

        order
    }

    override suspend fun findById(orderId: String): Order? = databaseFactory.dbQuery {
        fetchOrder(orderId.toUuid("заказа"))
    }

    override suspend fun listByUser(userId: String): List<Order> = databaseFactory.dbQuery {
        val userUuid = userId.toUuid("пользователя")
        OrdersTable.selectAll()
            .where { OrdersTable.userId eq userUuid }
            .orderBy(OrdersTable.createdAt to SortOrder.DESC)
            .map { row -> row.toOrder(fetchItems(row[OrdersTable.id])) }
    }

    override suspend fun cancel(order: Order, auditAction: String, auditDetails: String): Order = databaseFactory.dbQuery {
        val orderUuid = order.id.toUuid("заказа")
        val userUuid = order.userId.toUuid("пользователя")

        order.items.forEach { item ->
            val productUuid = item.productId.toUuid("товара")
            val currentProduct = ProductsTable.selectAll()
                .where { ProductsTable.id eq productUuid }
                .single()

            ProductsTable.update({ ProductsTable.id eq productUuid }) {
                it[stock] = currentProduct[ProductsTable.stock] + item.quantity
                it[updatedAt] = order.updatedAt
            }
        }

        OrdersTable.update({ OrdersTable.id eq orderUuid }) {
            it[status] = order.status.name
            it[updatedAt] = order.updatedAt
        }

        insertAuditLog(
            entityType = "order",
            entityId = order.id,
            action = auditAction,
            userId = userUuid,
            details = auditDetails,
        )

        fetchOrder(orderUuid) ?: throw NotFoundException("Заказ не найден")
    }

    override suspend fun getStats(): OrderStats = databaseFactory.dbQuery {
        val allOrders = OrdersTable.selectAll().toList()
        val totalOrders = allOrders.size.toLong()
        val createdOrders = allOrders.count { it[OrdersTable.status] == OrderStatus.CREATED.name }.toLong()
        val cancelledOrders = allOrders.count { it[OrdersTable.status] == OrderStatus.CANCELLED.name }.toLong()
        val totalRevenue = allOrders
            .filter { it[OrdersTable.status] == OrderStatus.CREATED.name }
            .sumOf { it[OrdersTable.total].toDouble() }
            .roundMoney()
        val uniqueCustomers = allOrders.map { it[OrdersTable.userId] }.toSet().size.toLong()

        OrderStats(
            totalOrders = totalOrders,
            createdOrders = createdOrders,
            cancelledOrders = cancelledOrders,
            totalRevenue = totalRevenue,
            uniqueCustomers = uniqueCustomers,
        )
    }

    private fun fetchOrder(orderId: UUID): Order? {
        val order = OrdersTable.selectAll()
            .where { OrdersTable.id eq orderId }
            .singleOrNull()
            ?: return null

        return order.toOrder(fetchItems(orderId))
    }

    private fun fetchItems(orderId: UUID): List<OrderItem> = OrderItemsTable.selectAll()
        .where { OrderItemsTable.orderId eq orderId }
        .map {
            OrderItem(
                productId = it[OrderItemsTable.productId].toString(),
                productName = it[OrderItemsTable.productName],
                price = it[OrderItemsTable.price].toDouble().roundMoney(),
                quantity = it[OrderItemsTable.quantity],
            )
        }

    private fun ResultRow.toOrder(items: List<OrderItem>): Order = Order(
        id = this[OrdersTable.id].toString(),
        userId = this[OrdersTable.userId].toString(),
        status = OrderStatus.valueOf(this[OrdersTable.status]),
        total = this[OrdersTable.total].toDouble().roundMoney(),
        items = items,
        createdAt = this[OrdersTable.createdAt],
        updatedAt = this[OrdersTable.updatedAt],
    )

    private fun insertAuditLog(
        entityType: String,
        entityId: String,
        action: String,
        userId: UUID?,
        details: String,
    ) {
        AuditLogsTable.insert {
            it[id] = UUID.randomUUID()
            it[this.entityType] = entityType
            it[this.entityId] = entityId
            it[this.action] = action
            it[this.userId] = userId
            it[this.details] = details
            it[createdAt] = currentTimestamp()
        }
    }
}

private fun String.toUuid(entityName: String): UUID = runCatching { UUID.fromString(this) }
    .getOrElse { throw ValidationException("Некорректный id для сущности: $entityName") }

private fun Double.toMoney(): BigDecimal = BigDecimal.valueOf(this).setScale(2, RoundingMode.HALF_UP)

private fun Double.roundMoney(): Double = toMoney().toDouble()

private fun currentTimestamp(): String = Instant.now().toString()

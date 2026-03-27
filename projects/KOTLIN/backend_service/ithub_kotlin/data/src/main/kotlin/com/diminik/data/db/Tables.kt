package com.diminik.data.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = uuid("id")
    val email = varchar("email", 255).uniqueIndex("uk_users_email")
    val fullName = varchar("full_name", 255)
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 32)
    val createdAt = varchar("created_at", 64)

    override val primaryKey = PrimaryKey(id)
}

object ProductsTable : Table("products") {
    val id = uuid("id")
    val name = varchar("name", 255).index("idx_products_name")
    val description = text("description").nullable()
    val price = decimal("price", 12, 2)
    val stock = integer("stock")
    val createdAt = varchar("created_at", 64)
    val updatedAt = varchar("updated_at", 64)

    override val primaryKey = PrimaryKey(id)
}

object OrdersTable : Table("orders") {
    val id = uuid("id")
    val userId = reference("user_id", UsersTable.id, onDelete = ReferenceOption.CASCADE).index("idx_orders_user_id")
    val status = varchar("status", 32).index("idx_orders_status")
    val total = decimal("total", 12, 2)
    val createdAt = varchar("created_at", 64)
    val updatedAt = varchar("updated_at", 64)

    override val primaryKey = PrimaryKey(id)
}

object OrderItemsTable : Table("order_items") {
    val id = uuid("id")
    val orderId = reference("order_id", OrdersTable.id, onDelete = ReferenceOption.CASCADE).index("idx_order_items_order_id")
    val productId = reference("product_id", ProductsTable.id)
    val productName = varchar("product_name", 255)
    val price = decimal("price", 12, 2)
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(id)
}

object AuditLogsTable : Table("audit_logs") {
    val id = uuid("id")
    val entityType = varchar("entity_type", 64).index("idx_audit_entity_type")
    val entityId = varchar("entity_id", 64).index("idx_audit_entity_id")
    val action = varchar("action", 64)
    val userId = uuid("user_id").nullable()
    val details = text("details")
    val createdAt = varchar("created_at", 64)

    override val primaryKey = PrimaryKey(id)
}

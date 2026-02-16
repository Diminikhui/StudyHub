package org.example.db

import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable("users") {
    val username = varchar("username", 64).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)
}

object Events : LongIdTable("events") {
    val title = varchar("title", 200)
    val description = varchar("description", 2000).nullable()
    val ownerId = reference("owner_id", Users) // one-to-many
}
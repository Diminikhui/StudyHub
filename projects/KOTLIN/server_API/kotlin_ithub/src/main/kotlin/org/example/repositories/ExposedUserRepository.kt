package org.example.repositories

import org.example.db.DatabaseFactory
import org.example.db.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class ExposedUserRepository(
    private val db: DatabaseFactory
) : UserRepository {

    override suspend fun create(username: String, passwordHash: String): Long = db.dbQuery {
        Users.insertAndGetId {
            it[Users.username] = username
            it[Users.passwordHash] = passwordHash
        }.value
    }

    override suspend fun findByUsername(username: String): UserRecord? = db.dbQuery {
        Users.selectAll()
            .where { Users.username eq username }
            .limit(1)
            .map(::toUserRecord)
            .singleOrNull()
    }

    override suspend fun findById(id: Long): UserRecord? = db.dbQuery {
        Users.selectAll()
            .where { Users.id eq id }
            .limit(1)
            .map(::toUserRecord)
            .singleOrNull()
    }

    private fun toUserRecord(row: ResultRow): UserRecord =
        UserRecord(
            id = row[Users.id].value,
            username = row[Users.username],
            passwordHash = row[Users.passwordHash]
        )
}
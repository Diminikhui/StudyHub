package org.example.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactory(
    private val driver: String,
    private val url: String,
    private val user: String,
    private val password: String
) {
    fun init() {
        Database.connect(url = url, driver = driver, user = user, password = password)
        transaction {
            SchemaUtils.create(Users, Events)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
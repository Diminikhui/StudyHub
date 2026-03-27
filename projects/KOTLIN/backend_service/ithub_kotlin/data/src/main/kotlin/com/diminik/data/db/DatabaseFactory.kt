package com.diminik.data.db

import com.diminik.core.config.DatabaseSettings
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DatabaseFactory(
    settings: DatabaseSettings,
) : AutoCloseable {
    private val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = settings.jdbcUrl
            username = settings.username
            password = settings.password
            maximumPoolSize = settings.maximumPoolSize
            driverClassName = "org.postgresql.Driver"
            validate()
        },
    )

    private val database: Database = Database.connect(dataSource)

    suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) {
            block()
        }

    override fun close() {
        dataSource.close()
    }
}

package com.diminik.core.config

data class AppSettings(
    val security: SecuritySettings,
    val database: DatabaseSettings,
    val redis: RedisSettings,
    val rabbitMq: RabbitMqSettings,
)

data class SecuritySettings(
    val issuer: String,
    val audience: String,
    val realm: String,
    val secret: String,
    val tokenTtlMinutes: Long,
    val bootstrapAdminEmail: String,
    val bootstrapAdminPassword: String,
    val bootstrapAdminName: String,
)

data class DatabaseSettings(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val maximumPoolSize: Int,
)

data class RedisSettings(
    val host: String,
    val port: Int,
    val password: String?,
    val database: Int,
    val productTtlSeconds: Long,
    val orderTtlSeconds: Long,
)

data class RabbitMqSettings(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val queueName: String,
)

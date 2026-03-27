package com.diminik.core.config

import io.ktor.server.config.ApplicationConfig

object ApplicationConfigLoader {
    fun load(config: ApplicationConfig): AppSettings = AppSettings(
        security = SecuritySettings(
            issuer = stringValue(config, "app.security.issuer", "JWT_ISSUER", "kotlinithub"),
            audience = stringValue(config, "app.security.audience", "JWT_AUDIENCE", "kotlinithub-users"),
            realm = stringValue(config, "app.security.realm", "JWT_REALM", "kotlinithub"),
            secret = stringValue(config, "app.security.secret", "JWT_SECRET", "change-me-please"),
            tokenTtlMinutes = longValue(config, "app.security.tokenTtlMinutes", "JWT_TTL_MINUTES", 120),
            bootstrapAdminEmail = stringValue(
                config,
                "app.security.bootstrapAdminEmail",
                "BOOTSTRAP_ADMIN_EMAIL",
                "admin@gmail.com",
            ),
            bootstrapAdminPassword = stringValue(
                config,
                "app.security.bootstrapAdminPassword",
                "BOOTSTRAP_ADMIN_PASSWORD",
                "admin123",
            ),
            bootstrapAdminName = stringValue(
                config,
                "app.security.bootstrapAdminName",
                "BOOTSTRAP_ADMIN_NAME",
                "Default Admin",
            ),
        ),
        database = DatabaseSettings(
            jdbcUrl = stringValue(
                config,
                "app.database.jdbcUrl",
                "JDBC_DATABASE_URL",
                "jdbc:postgresql://localhost:5432/kotlinithub",
            ),
            username = stringValue(config, "app.database.username", "JDBC_DATABASE_USERNAME", "kotlinithub"),
            password = stringValue(config, "app.database.password", "JDBC_DATABASE_PASSWORD", "kotlinithub"),
            maximumPoolSize = intValue(config, "app.database.maximumPoolSize", "DB_POOL_SIZE", 10),
        ),
        redis = RedisSettings(
            host = stringValue(config, "app.redis.host", "REDIS_HOST", "localhost"),
            port = intValue(config, "app.redis.port", "REDIS_PORT", 6379),
            password = nullableStringValue(config, "app.redis.password", "REDIS_PASSWORD"),
            database = intValue(config, "app.redis.database", "REDIS_DATABASE", 0),
            productTtlSeconds = longValue(config, "app.redis.productTtlSeconds", "PRODUCT_CACHE_TTL", 300),
            orderTtlSeconds = longValue(config, "app.redis.orderTtlSeconds", "ORDER_CACHE_TTL", 900),
        ),
        rabbitMq = RabbitMqSettings(
            host = stringValue(config, "app.rabbitmq.host", "RABBITMQ_HOST", "localhost"),
            port = intValue(config, "app.rabbitmq.port", "RABBITMQ_PORT", 5672),
            username = stringValue(config, "app.rabbitmq.username", "RABBITMQ_USERNAME", "guest"),
            password = stringValue(config, "app.rabbitmq.password", "RABBITMQ_PASSWORD", "guest"),
            queueName = stringValue(config, "app.rabbitmq.queueName", "RABBITMQ_QUEUE", "order-events"),
        ),
    )

    private fun stringValue(
        config: ApplicationConfig,
        configPath: String,
        envName: String,
        defaultValue: String,
    ): String = System.getenv(envName)
        ?.takeIf { it.isNotBlank() }
        ?: config.propertyOrNull(configPath)?.getString()
        ?: defaultValue

    private fun nullableStringValue(
        config: ApplicationConfig,
        configPath: String,
        envName: String,
    ): String? = System.getenv(envName)
        ?.takeIf { it.isNotBlank() }
        ?: config.propertyOrNull(configPath)?.getString()
        ?.takeIf { it.isNotBlank() }

    private fun intValue(
        config: ApplicationConfig,
        configPath: String,
        envName: String,
        defaultValue: Int,
    ): Int = stringValue(config, configPath, envName, defaultValue.toString()).toInt()

    private fun longValue(
        config: ApplicationConfig,
        configPath: String,
        envName: String,
        defaultValue: Long,
    ): Long = stringValue(config, configPath, envName, defaultValue.toString()).toLong()
}

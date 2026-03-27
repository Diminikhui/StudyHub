package com.diminik.api.di

import com.diminik.core.config.ApplicationConfigLoader
import com.diminik.core.config.SecuritySettings
import com.diminik.core.json.AppJson
import com.diminik.core.security.BcryptPasswordHasher
import com.diminik.core.security.JwtTokenIssuer
import com.diminik.data.cache.RedisClientFactory
import com.diminik.data.cache.RedisOrderCache
import com.diminik.data.cache.RedisProductCache
import com.diminik.data.db.DatabaseFactory
import com.diminik.data.db.MigrationRunner
import com.diminik.data.messaging.LoggingEmailSender
import com.diminik.data.messaging.RabbitMqClient
import com.diminik.data.messaging.RabbitMqOrderEventPublisher
import com.diminik.data.messaging.RabbitMqOrderEventWorker
import com.diminik.data.repository.ExposedOrderRepository
import com.diminik.data.repository.ExposedProductRepository
import com.diminik.data.repository.ExposedUserRepository
import com.diminik.domain.model.RegisterCommand
import com.diminik.domain.service.AuthService
import com.diminik.domain.service.OrderService
import com.diminik.domain.service.ProductService
import io.ktor.server.config.ApplicationConfig
import kotlinx.coroutines.runBlocking

data class AppComponents(
    val authService: AuthService,
    val productService: ProductService,
    val orderService: OrderService,
    val jwtTokenIssuer: JwtTokenIssuer,
    val securitySettings: SecuritySettings,
    val close: () -> Unit,
)

object AppFactory {
    fun create(config: ApplicationConfig, testing: Boolean): AppComponents {
        val settings = ApplicationConfigLoader.load(config)
        val json = AppJson.default

        MigrationRunner.migrate(settings.database)
        val databaseFactory = DatabaseFactory(settings.database)
        val redisClientFactory = RedisClientFactory(settings.redis)
        val rabbitMqClient = RabbitMqClient(settings.rabbitMq)

        val userRepository = ExposedUserRepository(databaseFactory)
        val productRepository = ExposedProductRepository(databaseFactory)
        val orderRepository = ExposedOrderRepository(databaseFactory)

        val productCache = RedisProductCache(
            client = redisClientFactory.client,
            ttlSeconds = settings.redis.productTtlSeconds,
            json = json,
        )
        val orderCache = RedisOrderCache(
            client = redisClientFactory.client,
            ttlSeconds = settings.redis.orderTtlSeconds,
            json = json,
        )

        val jwtTokenIssuer = JwtTokenIssuer(settings.security)
        val authService = AuthService(
            userRepository = userRepository,
            passwordHasher = BcryptPasswordHasher(),
            tokenIssuer = jwtTokenIssuer,
        )
        val productService = ProductService(productRepository, productCache)
        val eventPublisher = RabbitMqOrderEventPublisher(rabbitMqClient, json)
        val orderService = OrderService(
            orderRepository = orderRepository,
            productRepository = productRepository,
            productCache = productCache,
            orderCache = orderCache,
            eventPublisher = eventPublisher,
        )
        val worker = RabbitMqOrderEventWorker(
            rabbitMqClient = rabbitMqClient,
            emailSender = LoggingEmailSender(),
            json = json,
        )

        runBlocking {
            authService.bootstrapAdmin(
                RegisterCommand(
                    email = settings.security.bootstrapAdminEmail,
                    password = settings.security.bootstrapAdminPassword,
                    fullName = settings.security.bootstrapAdminName,
                ),
            )
        }

        if (!testing) {
            worker.start()
        }

        return AppComponents(
            authService = authService,
            productService = productService,
            orderService = orderService,
            jwtTokenIssuer = jwtTokenIssuer,
            securitySettings = settings.security,
            close = {
                worker.close()
                rabbitMqClient.close()
                redisClientFactory.close()
                databaseFactory.close()
            },
        )
    }
}

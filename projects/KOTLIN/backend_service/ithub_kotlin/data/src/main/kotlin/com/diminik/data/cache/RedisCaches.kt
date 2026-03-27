package com.diminik.data.cache

import com.diminik.core.config.RedisSettings
import com.diminik.domain.model.Order
import com.diminik.domain.model.Product
import com.diminik.domain.ports.OrderCache
import com.diminik.domain.ports.ProductCache
import java.util.concurrent.TimeUnit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config

class RedisClientFactory(
    settings: RedisSettings,
) : AutoCloseable {
    val client: RedissonClient = Redisson.create(redisConfig(settings))

    override fun close() {
        client.shutdown()
    }

    private fun redisConfig(settings: RedisSettings): Config = Config().apply {
        useSingleServer().apply {
            address = "redis://${settings.host}:${settings.port}"
            database = settings.database
            settings.password
                ?.takeIf { it.isNotBlank() }
                ?.let { password = it }
        }
    }
}

class RedisProductCache(
    private val client: RedissonClient,
    private val ttlSeconds: Long,
    private val json: Json,
) : ProductCache {
    override suspend fun get(productId: String): Product? = client.getBucket<String>(key(productId), StringCodec.INSTANCE)
        .get()
        ?.let { json.decodeFromString<Product>(it) }

    override suspend fun put(product: Product) {
        client.getBucket<String>(key(product.id), StringCodec.INSTANCE)
            .set(json.encodeToString(product), ttlSeconds, TimeUnit.SECONDS)
    }

    override suspend fun invalidate(productId: String) {
        client.getBucket<String>(key(productId), StringCodec.INSTANCE).delete()
    }

    private fun key(productId: String): String = "product:$productId"
}

class RedisOrderCache(
    private val client: RedissonClient,
    private val ttlSeconds: Long,
    private val json: Json,
) : OrderCache {
    override suspend fun get(orderId: String): Order? = client.getBucket<String>(key(orderId), StringCodec.INSTANCE)
        .get()
        ?.let { json.decodeFromString<Order>(it) }

    override suspend fun put(order: Order) {
        client.getBucket<String>(key(order.id), StringCodec.INSTANCE)
            .set(json.encodeToString(order), ttlSeconds, TimeUnit.SECONDS)
    }

    override suspend fun invalidate(orderId: String) {
        client.getBucket<String>(key(orderId), StringCodec.INSTANCE).delete()
    }

    private fun key(orderId: String): String = "order:$orderId"
}

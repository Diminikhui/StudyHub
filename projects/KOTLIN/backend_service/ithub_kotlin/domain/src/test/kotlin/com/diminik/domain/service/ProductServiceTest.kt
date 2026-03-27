package com.diminik.domain.service

import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.Product
import com.diminik.domain.model.UpdateProductCommand
import com.diminik.domain.ports.ProductCache
import com.diminik.domain.ports.ProductRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductServiceTest {
    @Test
    fun `update refreshes cache`() = runTest {
        val repository = FakeProductRepository()
        val cache = FakeProductCache()
        val service = ProductService(repository, cache)
        val product = service.create(CreateProductCommand("Keyboard", "Hot swap", 120.0, 4))

        val updated = service.update(
            product.id,
            UpdateProductCommand("Keyboard Pro", "Hot swap", 150.0, 7),
        )

        assertEquals("Keyboard Pro", updated.name)
        assertTrue(cache.invalidatedIds.contains(product.id))
        assertEquals(updated, cache.items[product.id])
    }
}

private class FakeProductRepository : ProductRepository {
    private val products = linkedMapOf<String, Product>()

    override suspend fun getAll(): List<Product> = products.values.toList()

    override suspend fun getById(id: String): Product? = products[id]

    override suspend fun create(command: CreateProductCommand): Product {
        val product = Product(
            id = "product-${products.size + 1}",
            name = command.name,
            description = command.description,
            price = command.price,
            stock = command.stock,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z",
        )
        products[product.id] = product
        return product
    }

    override suspend fun update(id: String, command: UpdateProductCommand): Product {
        val updated = products.getValue(id).copy(
            name = command.name,
            description = command.description,
            price = command.price,
            stock = command.stock,
            updatedAt = "2026-01-01T01:00:00Z",
        )
        products[id] = updated
        return updated
    }

    override suspend fun delete(id: String): Boolean = products.remove(id) != null
}

private class FakeProductCache : ProductCache {
    val items = linkedMapOf<String, Product>()
    val invalidatedIds = mutableListOf<String>()

    override suspend fun get(productId: String): Product? = items[productId]

    override suspend fun put(product: Product) {
        items[product.id] = product
    }

    override suspend fun invalidate(productId: String) {
        invalidatedIds += productId
        items.remove(productId)
    }
}

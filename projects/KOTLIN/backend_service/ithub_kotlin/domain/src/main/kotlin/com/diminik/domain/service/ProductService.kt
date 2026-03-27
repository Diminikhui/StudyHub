package com.diminik.domain.service

import com.diminik.domain.exception.NotFoundException
import com.diminik.domain.exception.ValidationException
import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.Product
import com.diminik.domain.model.UpdateProductCommand
import com.diminik.domain.ports.ProductCache
import com.diminik.domain.ports.ProductRepository

class ProductService(
    private val productRepository: ProductRepository,
    private val productCache: ProductCache,
) {
    suspend fun getAll(): List<Product> = productRepository.getAll()

    suspend fun getById(id: String): Product {
        validateId(id, "товар")
        productCache.get(id)?.let { return it }

        val product = productRepository.getById(id)
            ?: throw NotFoundException("Товар не найден")
        productCache.put(product)
        return product
    }

    suspend fun create(command: CreateProductCommand): Product {
        validateCommand(command.name, command.price, command.stock)
        val product = productRepository.create(command)
        productCache.put(product)
        return product
    }

    suspend fun update(id: String, command: UpdateProductCommand): Product {
        validateId(id, "товар")
        validateCommand(command.name, command.price, command.stock)
        val product = productRepository.update(id, command)
        productCache.invalidate(id)
        productCache.put(product)
        return product
    }

    suspend fun delete(id: String) {
        validateId(id, "товар")
        val deleted = productRepository.delete(id)
        if (!deleted) {
            throw NotFoundException("Товар не найден")
        }
        productCache.invalidate(id)
    }

    private fun validateCommand(name: String, price: Double, stock: Int) {
        if (name.isBlank()) {
            throw ValidationException("Название товара не может быть пустым")
        }
        if (price <= 0.0) {
            throw ValidationException("Цена товара должна быть больше нуля")
        }
        if (stock < 0) {
            throw ValidationException("Количество на складе не может быть отрицательным")
        }
    }

    private fun validateId(id: String, entityName: String) {
        if (id.isBlank()) {
            throw ValidationException("Нужно указать id для сущности: $entityName")
        }
    }
}

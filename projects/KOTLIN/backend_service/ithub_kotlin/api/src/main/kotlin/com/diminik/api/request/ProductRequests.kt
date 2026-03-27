package com.diminik.api.request

import com.diminik.domain.model.CreateProductCommand
import com.diminik.domain.model.UpdateProductCommand
import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
) {
    fun toCommand(): CreateProductCommand = CreateProductCommand(
        name = name,
        description = description,
        price = price,
        stock = stock,
    )
}

@Serializable
data class UpdateProductRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
) {
    fun toCommand(): UpdateProductCommand = UpdateProductCommand(
        name = name,
        description = description,
        price = price,
        stock = stock,
    )
}

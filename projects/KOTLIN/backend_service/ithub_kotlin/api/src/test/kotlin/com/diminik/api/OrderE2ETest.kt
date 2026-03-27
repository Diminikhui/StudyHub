package com.diminik.api

import com.diminik.core.json.AppJson
import com.diminik.domain.model.AuthResponse
import com.diminik.domain.model.Order
import com.diminik.domain.model.Product
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderE2ETest {
    @Test
    fun `admin creates product and user creates order`() = testApplication {
        application {
            configuredModule(testing = true, externalComponents = TestAppFactory.createComponents())
        }

        val adminToken = login("admin@test.local", "admin123")
        val createdProduct = createProduct(adminToken)

        val userRegisterResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "email": "buyer@test.local",
                  "password": "secret123",
                  "fullName": "Buyer Test"
                }
                """.trimIndent(),
            )
        }
        val userToken = AppJson.default.decodeFromString<AuthResponse>(userRegisterResponse.bodyAsText()).token

        val orderResponse = client.post("/orders") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $userToken")
            setBody(
                """
                {
                  "items": [
                    {
                      "productId": "${createdProduct.id}",
                      "quantity": 2
                    }
                  ]
                }
                """.trimIndent(),
            )
        }
        val order = AppJson.default.decodeFromString<Order>(orderResponse.bodyAsText())

        val historyResponse = client.get("/orders") {
            header(HttpHeaders.Authorization, "Bearer $userToken")
        }
        val orders = AppJson.default.decodeFromString(
            ListSerializer(Order.serializer()),
            historyResponse.bodyAsText(),
        )

        assertEquals(HttpStatusCode.Created, orderResponse.status)
        assertEquals(1, orders.size)
        assertEquals(createdProduct.id, order.items.first().productId)
        assertEquals(2, order.items.first().quantity)
    }

    private suspend fun io.ktor.server.testing.ApplicationTestBuilder.login(email: String, password: String): String {
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "email": "$email",
                  "password": "$password"
                }
                """.trimIndent(),
            )
        }
        return AppJson.default.decodeFromString<AuthResponse>(response.bodyAsText()).token
    }

    private suspend fun io.ktor.server.testing.ApplicationTestBuilder.createProduct(token: String): Product {
        val response = client.post("/products") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(
                """
                {
                  "name": "Laptop",
                  "description": "16GB RAM",
                  "price": 1499.99,
                  "stock": 5
                }
                """.trimIndent(),
            )
        }
        return AppJson.default.decodeFromString<Product>(response.bodyAsText())
    }
}

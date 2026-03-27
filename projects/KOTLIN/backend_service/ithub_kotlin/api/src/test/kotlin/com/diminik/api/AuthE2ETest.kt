package com.diminik.api

import com.diminik.core.json.AppJson
import com.diminik.domain.model.AuthResponse
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthE2ETest {
    @Test
    fun `register and login endpoints return jwt`() = testApplication {
        application {
            configuredModule(testing = true, externalComponents = TestAppFactory.createComponents())
        }

        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "email": "user@test.local",
                  "password": "secret123",
                  "fullName": "User Test"
                }
                """.trimIndent(),
            )
        }
        val authResponse = AppJson.default.decodeFromString<AuthResponse>(registerResponse.bodyAsText())

        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "email": "user@test.local",
                  "password": "secret123"
                }
                """.trimIndent(),
            )
        }
        val loginAuthResponse = AppJson.default.decodeFromString<AuthResponse>(loginResponse.bodyAsText())

        assertEquals(HttpStatusCode.Created, registerResponse.status)
        assertTrue(authResponse.token.isNotBlank())
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        assertTrue(loginAuthResponse.token.isNotBlank())
        assertEquals("user@test.local", loginAuthResponse.user.email)
    }
}

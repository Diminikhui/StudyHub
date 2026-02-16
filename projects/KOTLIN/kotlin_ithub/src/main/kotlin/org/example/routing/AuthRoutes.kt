package org.example.routing

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.models.LoginRequest
import org.example.models.RegisterRequest
import org.example.models.TokenResponse
import org.example.services.AuthService
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService by inject<AuthService>()

    post("/auth/register") {
        val req = call.receive<RegisterRequest>()
        val userId = authService.register(req.username, req.password)
        call.respond(mapOf("userId" to userId))
    }

    post("/auth/login") {
        val req = call.receive<LoginRequest>()
        val token = authService.login(req.username, req.password)
        call.respond(TokenResponse(token))
    }
}
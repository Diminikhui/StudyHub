package com.diminik.api.routes

import com.diminik.api.request.LoginRequest
import com.diminik.api.request.RegisterRequest
import com.diminik.domain.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val response = authService.register(request.toCommand())
            call.respond(HttpStatusCode.Created, response)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = authService.login(request.toCommand())
            call.respond(HttpStatusCode.OK, response)
        }
    }
}

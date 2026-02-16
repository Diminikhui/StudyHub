package org.example.routing

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        eventRoutes()
        debugRoutes()
    }
}
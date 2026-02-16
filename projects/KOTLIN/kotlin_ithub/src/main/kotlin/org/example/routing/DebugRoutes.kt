package org.example.routing

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.models.EventResponse
import org.example.services.AuthService
import org.example.services.EventService
import org.koin.ktor.ext.inject

fun Route.debugRoutes() {
    val authService by inject<AuthService>()
    val eventService by inject<EventService>()

    get("/") { call.respondText("OK") }

    post("/debug/seed") {
        val userId = authService.registerIfMissing("test", "password123")
        val created = eventService.create(
            ownerId = userId,
            title = "Hello",
            description = "seed event"
        )
        call.respond(EventResponse(created.id, created.title, created.description, created.ownerId))
    }
}
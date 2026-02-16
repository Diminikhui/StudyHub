package org.example.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable
import org.example.models.CreateEventRequest
import org.example.models.EventResponse
import org.example.models.UpdateEventRequest
import org.example.services.EventService
import org.koin.ktor.ext.inject

@Serializable
data class EventsResponse(val events: List<EventResponse>)

fun Route.eventRoutes() {
    val eventService by inject<EventService>()

    // Public list (can be made protected later if you want)
    get("/events") {
        val events = eventService.listAll().map {
            EventResponse(it.id, it.title, it.description, it.ownerId)
        }
        call.respond(EventsResponse(events))
    }

    authenticate("auth-jwt") {
        post("/events") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asLong()

            val req = call.receive<CreateEventRequest>()
            val created = eventService.create(
                ownerId = userId,
                title = req.title,
                description = req.description
            )

            call.respond(EventResponse(created.id, created.title, created.description, created.ownerId))
        }

        put("/events/{id}") {
            val principal = call.principal<JWTPrincipal>()!!
            val userId = principal.payload.getClaim("userId").asLong()

            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id"))

            val req = call.receive<UpdateEventRequest>()

            val updated = eventService.updateIfOwner(
                id = id,
                ownerId = userId,
                title = req.title,
                description = req.description
            ) ?: return@put call.respond(
                HttpStatusCode.Forbidden,
                mapOf("error" to "Not owner or not found")
            )

            call.respond(EventResponse(updated.id, updated.title, updated.description, updated.ownerId))
        }
    }
}
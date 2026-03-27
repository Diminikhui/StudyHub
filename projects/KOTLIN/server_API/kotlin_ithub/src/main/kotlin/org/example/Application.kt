package org.example

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.example.plugins.configureDI
import org.example.plugins.configureSecurity
import org.example.routing.configureRouting
import org.slf4j.event.Level

fun Application.module() {
    configureDI()
    configureSecurity()

    install(ContentNegotiation) { json() }

    install(CallLogging) {
        level = Level.INFO
    }

    // твой кастомный плагин (если есть в plugins)
    install(org.example.plugins.RequestTimingPlugin)

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf(
                    "error" to (cause.message ?: "Bad Request"),
                    "type" to (cause::class.simpleName ?: "IllegalArgumentException")
                )
            )
        }

        exception<IllegalStateException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf(
                    "error" to (cause.message ?: "Unauthorized"),
                    "type" to (cause::class.simpleName ?: "IllegalStateException")
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf(
                    "error" to (cause.message ?: "Internal Server Error"),
                    "type" to (cause::class.simpleName ?: "Throwable")
                )
            )
        }
    }

    configureRouting()
}
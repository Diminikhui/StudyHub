package com.diminik.api.routes

import com.diminik.core.plugins.requireAdmin
import com.diminik.core.plugins.requireUserPrincipal
import com.diminik.domain.service.OrderService
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.adminRoutes(orderService: OrderService) {
    authenticate("auth-jwt") {
        get("/stats/orders") {
            call.requireUserPrincipal().requireAdmin()
            call.respond(orderService.getStats())
        }
    }
}

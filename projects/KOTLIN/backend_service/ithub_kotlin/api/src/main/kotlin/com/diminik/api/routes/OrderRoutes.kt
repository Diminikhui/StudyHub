package com.diminik.api.routes

import com.diminik.api.request.CreateOrderRequest
import com.diminik.core.plugins.requireUserPrincipal
import com.diminik.domain.service.OrderService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.orderRoutes(orderService: OrderService) {
    authenticate("auth-jwt") {
        route("/orders") {
            post {
                val request = call.receive<CreateOrderRequest>()
                val principal = call.requireUserPrincipal()
                val order = orderService.create(principal.id, request.toCommand())
                call.respond(HttpStatusCode.Created, order)
            }

            get {
                val principal = call.requireUserPrincipal()
                call.respond(orderService.getHistory(principal.id))
            }

            delete("/{id}") {
                val principal = call.requireUserPrincipal()
                val orderId = call.parameters["id"].orEmpty()
                call.respond(orderService.cancel(orderId, principal.id))
            }
        }
    }
}

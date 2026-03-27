package com.diminik.api.routes

import com.diminik.api.request.CreateProductRequest
import com.diminik.api.request.UpdateProductRequest
import com.diminik.core.plugins.requireAdmin
import com.diminik.core.plugins.requireUserPrincipal
import com.diminik.domain.service.ProductService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.productRoutes(productService: ProductService) {
    route("/products") {
        get {
            call.respond(productService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"].orEmpty()
            call.respond(productService.getById(id))
        }

        authenticate("auth-jwt") {
            post {
                call.requireUserPrincipal().requireAdmin()
                val request = call.receive<CreateProductRequest>()
                val product = productService.create(request.toCommand())
                call.respond(HttpStatusCode.Created, product)
            }

            put("/{id}") {
                call.requireUserPrincipal().requireAdmin()
                val request = call.receive<UpdateProductRequest>()
                val id = call.parameters["id"].orEmpty()
                call.respond(productService.update(id, request.toCommand()))
            }

            delete("/{id}") {
                call.requireUserPrincipal().requireAdmin()
                val id = call.parameters["id"].orEmpty()
                productService.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

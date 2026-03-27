package com.diminik.api.routes

import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.Route

fun Route.documentationRoutes() {
    openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
}

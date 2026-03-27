package com.diminik.api

import com.diminik.api.di.AppComponents
import com.diminik.api.di.AppFactory
import com.diminik.api.routes.adminRoutes
import com.diminik.api.routes.authRoutes
import com.diminik.api.routes.documentationRoutes
import com.diminik.api.routes.orderRoutes
import com.diminik.api.routes.productRoutes
import com.diminik.core.plugins.configureErrorHandling
import com.diminik.core.plugins.configureHttp
import com.diminik.core.plugins.configureMonitoring
import com.diminik.core.plugins.configureSecurity
import com.diminik.core.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.netty.EngineMain
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.response.respondText

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configuredModule(testing = false, externalComponents = null)
}

fun Application.configuredModule(
    testing: Boolean,
    externalComponents: AppComponents?,
) {
    val components = externalComponents ?: AppFactory.create(environment.config, testing)

    configureSerialization()
    configureMonitoring()
    configureHttp()
    configureErrorHandling()
    configureSecurity(
        jwtTokenIssuer = components.jwtTokenIssuer,
        securitySettings = components.securitySettings,
    )

    routing {
        get("/") {
            call.respondText("kotlinithub backend is running")
        }

        get("/health") {
            call.respondText("OK")
        }

        documentationRoutes()
        authRoutes(components.authService)
        productRoutes(components.productService)
        orderRoutes(components.orderService)
        adminRoutes(components.orderService)
    }

    monitor.subscribe(ApplicationStopped) {
        environment.log.info("Stopping application resources")
        components.close()
    }
}

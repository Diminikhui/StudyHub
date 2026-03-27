package com.diminik.core.plugins

import com.diminik.core.config.SecuritySettings
import com.diminik.core.json.AppJson
import com.diminik.core.security.JwtTokenIssuer
import com.diminik.core.security.UserPrincipal
import com.diminik.domain.exception.DomainException
import com.diminik.domain.exception.ForbiddenException
import com.diminik.domain.exception.UnauthorizedException
import com.diminik.domain.model.ErrorResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.path
import io.ktor.server.response.respond
import org.slf4j.event.Level

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(AppJson.default)
    }
}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }
}

fun Application.configureHttp() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Accept)
        anyHost()
    }

    install(DefaultHeaders) {
        header("X-App-Name", "kotlinithub")
    }
}

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<DomainException> { call, cause ->
            this@configureErrorHandling.environment.log.warn(
                "Domain error on {}: {} ({})",
                call.request.path(),
                cause.code,
                cause.message,
            )
            call.respond(
                status = HttpStatusCode.fromValue(cause.statusCode),
                message = ErrorResponse(cause.code, cause.message ?: "Ошибка бизнес-логики"),
            )
        }

        exception<Throwable> { call, cause ->
            this@configureErrorHandling.environment.log.error("Unhandled error", cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(
                    code = "INTERNAL_ERROR",
                    message = "Внутренняя ошибка сервера",
                ),
            )
        }
    }
}

fun Application.configureSecurity(
    jwtTokenIssuer: JwtTokenIssuer,
    securitySettings: SecuritySettings,
) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = securitySettings.realm
            verifier(jwtTokenIssuer.verifier)
            validate { credentials ->
                val userId = credentials.payload.getClaim("userId").asString()
                val email = credentials.payload.getClaim("email").asString()
                val role = credentials.payload.getClaim("role").asString()
                if (userId.isNullOrBlank() || email.isNullOrBlank() || role.isNullOrBlank()) {
                    null
                } else {
                    jwtTokenIssuer.principalFrom(JWTPrincipal(credentials.payload))
                }
            }
            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = ErrorResponse(
                        code = "UNAUTHORIZED",
                        message = "Нужен корректный JWT токен",
                    ),
                )
            }
        }
    }
}

fun ApplicationCall.requireUserPrincipal(): UserPrincipal = principal<UserPrincipal>()
    ?: throw UnauthorizedException("Пользователь не авторизован")

fun UserPrincipal.requireAdmin() {
    if (role != com.diminik.domain.model.UserRole.ADMIN) {
        throw ForbiddenException("Доступно только администратору")
    }
}

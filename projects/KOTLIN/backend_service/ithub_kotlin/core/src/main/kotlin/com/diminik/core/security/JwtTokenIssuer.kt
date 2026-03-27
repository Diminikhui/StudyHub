package com.diminik.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.diminik.core.config.SecuritySettings
import com.diminik.domain.model.User
import com.diminik.domain.model.UserRole
import com.diminik.domain.ports.TokenIssuer
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date

class JwtTokenIssuer(
    private val settings: SecuritySettings,
) : TokenIssuer {
    private val algorithm = Algorithm.HMAC256(settings.secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(settings.issuer)
        .withAudience(settings.audience)
        .build()

    override fun issue(user: User): String = JWT.create()
        .withIssuer(settings.issuer)
        .withAudience(settings.audience)
        .withClaim("userId", user.id)
        .withClaim("email", user.email)
        .withClaim("role", user.role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + settings.tokenTtlMinutes * 60_000))
        .sign(algorithm)

    fun principalFrom(jwtPrincipal: JWTPrincipal): UserPrincipal = UserPrincipal(
        id = jwtPrincipal.payload.getClaim("userId").asString(),
        email = jwtPrincipal.payload.getClaim("email").asString(),
        role = UserRole.valueOf(jwtPrincipal.payload.getClaim("role").asString()),
    )
}

data class UserPrincipal(
    val id: String,
    val email: String,
    val role: UserRole,
)

package org.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtService(
    private val issuer: String,
    private val audience: String,
    secret: String,
    private val expiresMs: Long
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun algorithm(): Algorithm = algorithm

    fun generateToken(userId: Long): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withExpiresAt(Date(now + expiresMs))
            .sign(algorithm)
    }
}
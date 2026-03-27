package org.example.services

import org.example.repositories.UserRepository

class AuthService(
    private val userRepo: UserRepository,
    private val hasher: PasswordHasher,
    private val jwt: JwtService
) {
    suspend fun register(username: String, password: String): Long {
        require(username.isNotBlank()) { "username is blank" }
        require(password.length >= 6) { "password too short" }

        val existing = userRepo.findByUsername(username.trim())
        require(existing == null) { "username already exists" }

        val hash = hasher.hash(password)
        return userRepo.create(username.trim(), hash)
    }

    suspend fun login(username: String, password: String): String {
        val user = userRepo.findByUsername(username.trim()) ?: error("invalid credentials")
        require(hasher.verify(password, user.passwordHash)) { "invalid credentials" }
        return jwt.generateToken(user.id)
    }

    // Used by /debug/seed
    suspend fun registerIfMissing(username: String, password: String): Long {
        val existing = userRepo.findByUsername(username.trim())
        if (existing != null) return existing.id
        val hash = hasher.hash(password)
        return userRepo.create(username.trim(), hash)
    }
}
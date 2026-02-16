package org.example.repositories

data class UserRecord(
    val id: Long,
    val username: String,
    val passwordHash: String
)

interface UserRepository {
    suspend fun create(username: String, passwordHash: String): Long
    suspend fun findByUsername(username: String): UserRecord?
    suspend fun findById(id: Long): UserRecord?
}
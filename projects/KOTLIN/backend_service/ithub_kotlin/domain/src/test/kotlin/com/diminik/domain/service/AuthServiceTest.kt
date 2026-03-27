package com.diminik.domain.service

import com.diminik.domain.exception.ConflictException
import com.diminik.domain.model.LoginCommand
import com.diminik.domain.model.RegisterCommand
import com.diminik.domain.model.User
import com.diminik.domain.model.UserCredentials
import com.diminik.domain.model.UserRole
import com.diminik.domain.ports.PasswordHasher
import com.diminik.domain.ports.TokenIssuer
import com.diminik.domain.ports.UserRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthServiceTest {
    @Test
    fun `register rejects duplicate email`() = runTest {
        val repository = FakeUserRepository()
        repository.create(
            command = RegisterCommand("user@example.com", "secret123", "Existing User"),
            passwordHash = "hashed-secret123",
        )
        val service = AuthService(repository, FakePasswordHasher(), FakeTokenIssuer())

        assertFailsWith<ConflictException> {
            service.register(RegisterCommand("user@example.com", "secret123", "Another User"))
        }
    }

    @Test
    fun `login returns token when credentials are valid`() = runTest {
        val repository = FakeUserRepository()
        repository.create(
            command = RegisterCommand("user@example.com", "secret123", "Valid User"),
            passwordHash = "hashed-secret123",
        )
        val service = AuthService(repository, FakePasswordHasher(), FakeTokenIssuer())

        val response = service.login(LoginCommand("user@example.com", "secret123"))

        assertEquals("token-user@example.com", response.token)
        assertEquals("user@example.com", response.user.email)
    }
}

private class FakeUserRepository : UserRepository {
    private val users = linkedMapOf<String, UserCredentials>()

    override suspend fun create(command: RegisterCommand, passwordHash: String): User {
        val user = User(
            id = command.email,
            email = command.email,
            fullName = command.fullName,
            role = UserRole.USER,
            createdAt = "2026-01-01T00:00:00Z",
        )
        users[user.email] = UserCredentials(user, passwordHash)
        return user
    }

    override suspend fun findCredentialsByEmail(email: String): UserCredentials? = users[email]

    override suspend fun findById(id: String): User? = users.values.firstOrNull { it.user.id == id }?.user

    override suspend fun existsByEmail(email: String): Boolean = users.containsKey(email)

    override suspend fun ensureAdminAccount(command: RegisterCommand, passwordHash: String): User =
        create(command, passwordHash)
}

private class FakePasswordHasher : PasswordHasher {
    override fun hash(rawPassword: String): String = "hashed-$rawPassword"

    override fun matches(rawPassword: String, passwordHash: String): Boolean = hash(rawPassword) == passwordHash
}

private class FakeTokenIssuer : TokenIssuer {
    override fun issue(user: User): String = "token-${user.email}"
}

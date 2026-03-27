package com.diminik.domain.service

import com.diminik.domain.exception.ConflictException
import com.diminik.domain.exception.UnauthorizedException
import com.diminik.domain.exception.ValidationException
import com.diminik.domain.model.AuthResponse
import com.diminik.domain.model.LoginCommand
import com.diminik.domain.model.RegisterCommand
import com.diminik.domain.ports.PasswordHasher
import com.diminik.domain.ports.TokenIssuer
import com.diminik.domain.ports.UserRepository

class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenIssuer: TokenIssuer,
) {
    suspend fun register(command: RegisterCommand): AuthResponse {
        val normalizedEmail = normalizeEmail(command.email)
        validateRegistration(command.copy(email = normalizedEmail))

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw ConflictException("Пользователь с таким email уже существует")
        }

        val user = userRepository.create(
            command = command.copy(email = normalizedEmail),
            passwordHash = passwordHasher.hash(command.password),
        )

        return AuthResponse(
            token = tokenIssuer.issue(user),
            user = user,
        )
    }

    suspend fun login(command: LoginCommand): AuthResponse {
        val normalizedEmail = normalizeEmail(command.email)
        val credentials = userRepository.findCredentialsByEmail(normalizedEmail)
            ?: throw UnauthorizedException("Неверный email или пароль")

        if (!passwordHasher.matches(command.password, credentials.passwordHash)) {
            throw UnauthorizedException("Неверный email или пароль")
        }

        return AuthResponse(
            token = tokenIssuer.issue(credentials.user),
            user = credentials.user,
        )
    }

    suspend fun bootstrapAdmin(command: RegisterCommand) {
        validateRegistration(command)
        userRepository.ensureAdminAccount(
            command = command.copy(email = normalizeEmail(command.email)),
            passwordHash = passwordHasher.hash(command.password),
        )
    }

    private fun validateRegistration(command: RegisterCommand) {
        if (command.fullName.isBlank()) {
            throw ValidationException("Имя не может быть пустым")
        }
        if (command.email.isBlank() || !command.email.contains("@")) {
            throw ValidationException("Нужно указать корректный email")
        }
        if (command.password.length < 6) {
            throw ValidationException("Пароль должен содержать минимум 6 символов")
        }
    }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()
}

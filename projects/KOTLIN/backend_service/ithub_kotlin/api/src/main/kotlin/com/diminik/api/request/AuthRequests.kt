package com.diminik.api.request

import com.diminik.domain.model.LoginCommand
import com.diminik.domain.model.RegisterCommand
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
) {
    fun toCommand(): RegisterCommand = RegisterCommand(
        email = email,
        password = password,
        fullName = fullName,
    )
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun toCommand(): LoginCommand = LoginCommand(
        email = email,
        password = password,
    )
}

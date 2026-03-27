package com.diminik.domain.exception

open class DomainException(
    val code: String,
    val statusCode: Int,
    message: String,
) : RuntimeException(message)

class ValidationException(message: String) : DomainException(
    code = "VALIDATION_ERROR",
    statusCode = 400,
    message = message,
)

class ConflictException(message: String) : DomainException(
    code = "CONFLICT",
    statusCode = 409,
    message = message,
)

class NotFoundException(message: String) : DomainException(
    code = "NOT_FOUND",
    statusCode = 404,
    message = message,
)

class UnauthorizedException(message: String) : DomainException(
    code = "UNAUTHORIZED",
    statusCode = 401,
    message = message,
)

class ForbiddenException(message: String) : DomainException(
    code = "FORBIDDEN",
    statusCode = 403,
    message = message,
)

class InsufficientStockException(message: String) : DomainException(
    code = "INSUFFICIENT_STOCK",
    statusCode = 409,
    message = message,
)

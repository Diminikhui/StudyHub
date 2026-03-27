package org.example.plugins

import io.ktor.server.application.*
import org.example.db.DatabaseFactory
import org.example.repositories.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.example.services.AuthService
import org.example.services.EventService
import org.example.services.PasswordHasher
import org.example.services.JwtService

fun Application.configureDI() {
    val cfg = environment.config

    val dbModule = module {
        single {
            DatabaseFactory(
                driver = cfg.property("db.driver").getString(),
                url = cfg.property("db.url").getString(),
                user = cfg.property("db.user").getString(),
                password = cfg.property("db.password").getString()
            ).also { it.init() }
        }

        // Repositories
        single<UserRepository> { ExposedUserRepository(get()) }
        single<EventRepository> { ExposedEventRepository(get()) }

        // Services
        single { PasswordHasher() }

        single {
            JwtService(
                issuer = cfg.property("jwt.issuer").getString(),
                audience = cfg.property("jwt.audience").getString(),
                secret = cfg.property("jwt.secret").getString(),
                expiresMs = cfg.property("jwt.expiresMs").getString().toLong()
            )
        }

        // AuthService: userRepo + hasher + jwt
        single { AuthService(get(), get(), get()) }

        single { EventService(get()) }
    }

    install(Koin) {
        modules(dbModule)
    }
}
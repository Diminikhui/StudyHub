plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

val ktorVersion = "2.3.12"

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")

    // Auth/JWT
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")

    // Koin
    implementation("io.insert-koin:koin-ktor:3.5.6")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.6")

    // Exposed + H2
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("com.h2database:h2:2.2.224")

    // BCrypt
    implementation("at.favre.lib:bcrypt:0.10.2")

    // Logging backend for SLF4J (чтобы не было NOP logger)
    implementation("ch.qos.logback:logback-classic:1.5.16")

    // Tests
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

tasks.test { useJUnitPlatform() }

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
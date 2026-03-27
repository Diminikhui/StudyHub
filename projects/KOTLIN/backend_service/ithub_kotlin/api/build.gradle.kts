import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by project
val logback_version: String by project
val junit_version: String by project
val kotlinx_coroutines_version: String by project
val kotlinx_serialization_version: String by project
val redisson_version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "com.diminik.api.ApplicationKt"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-openapi:$ktor_version")
    implementation("io.ktor:ktor-server-swagger:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")
    implementation("org.redisson:redisson:$redisson_version")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinx_coroutines_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
}

ktor {
    fatJar {
        archiveFileName.set("api-all.jar")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    mergeServiceFiles()
}

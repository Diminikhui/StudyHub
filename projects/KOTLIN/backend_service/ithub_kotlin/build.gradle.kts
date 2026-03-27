import org.gradle.api.tasks.testing.Test

plugins {
    kotlin("jvm") version "2.3.0" apply false
    kotlin("plugin.serialization") version "2.3.0" apply false
    id("io.ktor.plugin") version "3.4.1" apply false
}

group = "com.diminik"
version = "1.0.0"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

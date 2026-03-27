package com.diminik.data.db

import com.diminik.core.config.DatabaseSettings
import org.flywaydb.core.Flyway

object MigrationRunner {
    fun migrate(settings: DatabaseSettings) {
        Flyway.configure()
            .dataSource(settings.jdbcUrl, settings.username, settings.password)
            .locations(
                "filesystem:data/src/main/resources/db/migration",
                "filesystem:db/migration",
            )
            .failOnMissingLocations(false)
            .sqlMigrationPrefix("V")
            .repeatableSqlMigrationPrefix("R")
            .sqlMigrationSeparator("__")
            .validateMigrationNaming(true)
            .load()
            .migrate()
    }
}

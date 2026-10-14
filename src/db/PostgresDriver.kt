package cat.freya.alloy.db

import com.zaxxer.hikari.HikariConfig

class PostgresDriver(val config: Driver.DatabaseConfig) : HikariDriver() {
    override val driverClass = "org.postgresql.Driver"

    override fun jdbcUrl(): String {
        val port = config.port ?: 5432u
        return "jdbc:postgresql://${config.host}:$port/${config.database}"
    }

    override fun configure(hikari: HikariConfig) {
        hikari.username = config.username
        hikari.password = config.password
    }
}

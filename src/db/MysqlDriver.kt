package cat.freya.alloy.db

import com.zaxxer.hikari.HikariConfig

class MysqlDriver(val config: Driver.DatabaseConfig) : HikariDriver() {
    override val driverClass = "com.mysql.cj.jdbc.Driver"

    override fun jdbcUrl(): String {
        val port = config.port ?: 3006u
        return "jdbc:mysql://${config.host}:$port/${config.database}"
    }

    override fun configure(hikari: HikariConfig) {
        hikari.username = config.username
        hikari.password = config.password
    }
}

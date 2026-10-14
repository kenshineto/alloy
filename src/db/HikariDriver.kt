package cat.freya.alloy.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

abstract class HikariDriver : Driver() {
    abstract val driverClass: String

    abstract fun jdbcUrl(): String

    abstract fun configure(hikari: HikariConfig)

    override fun connect(): DataSource {
        // load driver for some reason
        Class.forName(driverClass)

        val cores = Runtime.getRuntime().availableProcessors()
        val hikari =
            HikariConfig().apply {
                jdbcUrl = jdbcUrl()
                driverClassName = driverClass
                maximumPoolSize = minOf(cores, 8)
                configure(this)
            }

        return HikariDataSource(hikari)
    }
}

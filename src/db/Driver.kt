package cat.freya.alloy.db

import cat.freya.alloy.annotation.*
import javax.sql.DataSource

abstract class Driver {
    abstract fun connect(): DataSource

    enum class DatabaseType {
        SQLITE,
        MEMORY,
        MYSQL,
        POSTGRES,
    }

    data class DatabaseConfig(
        @Comment("The type of database to store user data in")
        @Comment("SQLITE - local file in plugin directory, fine for most small servers")
        @Comment("MYSQL - remote sql server running mysql")
        @Comment("POSTGRES - remote sql server running postgresql")
        var type: DatabaseType = DatabaseType.SQLITE,
        @Comment("The following options are only required for mysql or postgres") var host: String = "localhost",
        var port: ULong? = null,
        var username: String = "postgres",
        var password: String = "postgres",
        var database: String = "postgres",
    )

    companion object {
        fun getDriver(config: DatabaseConfig, sqlitePath: String): Driver =
            when (config.type) {
                DatabaseType.SQLITE -> SqliteDriver(sqlitePath)
                DatabaseType.MEMORY -> SqliteDriver("file::memory:?cache=shared")
                DatabaseType.MYSQL -> MysqlDriver(config)
                DatabaseType.POSTGRES -> PostgresDriver(config)
            }
    }
}

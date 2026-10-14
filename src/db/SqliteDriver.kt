package cat.freya.alloy.db

import java.sql.Connection
import javax.sql.DataSource
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteDataSource

class SqliteDriver(val path: String) : Driver() {
    private val driverClass = "org.sqlite.JDBC"
    private val jdbcUrl = "jdbc:sqlite:$path"

    companion object {
        private var memCon: Connection? = null
    }

    override fun connect(): DataSource {
        Class.forName(driverClass)

        val config = SQLiteConfig()
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL)
        config.setTempStore(SQLiteConfig.TempStore.MEMORY)

        val source = SQLiteDataSource(config)
        source.url = jdbcUrl

        // keep in memory database alive
        if (path.startsWith("file::memory:") && memCon?.isClosed != false) {
            memCon = source.connection
        }

        return WrappedDataSource(source)
    }
}

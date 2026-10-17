package id.renaldi.proximity.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DatabaseDriverFactory(
    private val jdbcUrl: String = JdbcSqliteDriver.IN_MEMORY,
) {
    actual fun create(): SqlDriver = JdbcSqliteDriver(jdbcUrl).also { driver ->
        ProximityDatabase.Schema.create(driver)
    }
}

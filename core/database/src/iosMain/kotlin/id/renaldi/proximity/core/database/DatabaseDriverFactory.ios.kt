package id.renaldi.proximity.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver = NativeSqliteDriver(
        schema = ProximityDatabase.Schema,
        name = DatabaseFactory.DATABASE_NAME,
    )
}

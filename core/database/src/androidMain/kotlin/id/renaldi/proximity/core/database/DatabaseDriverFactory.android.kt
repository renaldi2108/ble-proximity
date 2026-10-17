package id.renaldi.proximity.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun create(): SqlDriver = AndroidSqliteDriver(
        schema = ProximityDatabase.Schema,
        context = context,
        name = DatabaseFactory.DATABASE_NAME,
    )
}

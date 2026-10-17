package id.renaldi.proximity.core.database

import app.cash.sqldelight.db.SqlDriver

object DatabaseFactory {
    const val DATABASE_NAME: String = "proximity.db"

    const val HISTORY_ROW_LIMIT: Long = 500L

    fun create(driver: SqlDriver): ProximityDatabase = ProximityDatabase(driver)
}

expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}

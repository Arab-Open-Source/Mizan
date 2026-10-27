package com.eec.core.db

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun createDatabase(context: Context): AppDatabase {
    val databaseFile = context.getDatabasePath(AppDatabase.NAME).apply {
        parentFile?.mkdirs()
    }
    return Room.databaseBuilder<AppDatabase>(databaseFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .addMigrations(*AppMigrations.all)
        .build()
}

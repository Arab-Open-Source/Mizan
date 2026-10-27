package com.eec.core.db

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.nio.file.Path

fun createDatabase(path: Path): AppDatabase =
    Room.databaseBuilder<AppDatabase>(path.toAbsolutePath().toString())
        .setDriver(BundledSQLiteDriver())
        .addMigrations(*AppMigrations.all)
        .build()

fun createInMemoryDatabase(): AppDatabase =
    Room.inMemoryDatabaseBuilder<AppDatabase>(AppDatabaseConstructor::initialize)
        .setDriver(BundledSQLiteDriver())
        .addMigrations(*AppMigrations.all)
        .setSingleConnectionPool()
        .build()

package com.eec.core.db

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import kotlin.js.ExperimentalWasmJsInterop
import org.w3c.dom.Worker

fun createDatabase(): AppDatabase =
    Room.databaseBuilder<AppDatabase>(AppDatabase.NAME)
        .setDriver(WebWorkerSQLiteDriver(createSqliteWorker()))
        .addMigrations(*AppMigrations.all)
        .build()

@OptIn(ExperimentalWasmJsInterop::class)
private fun createSqliteWorker(): Worker =
    js("""new Worker(new URL("sqlite-worker.js", import.meta.url), { type: "module" })""")

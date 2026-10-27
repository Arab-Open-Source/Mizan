package com.eec.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.eec.core.db.AppDatabase
import com.eec.core.db.createDatabase
import java.nio.file.Path

fun main() {
    val database = createDatabase(Path.of(AppDatabase.NAME))
    application {
        Window(onCloseRequest = ::exitApplication, title = "Mizan") {
            App(database)
        }
    }
}

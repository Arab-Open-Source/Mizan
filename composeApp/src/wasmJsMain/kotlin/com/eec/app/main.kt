package com.eec.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.eec.core.db.createDatabase
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val database = createDatabase()
    ComposeViewport(document.body!!) {
        App(database)
    }
}

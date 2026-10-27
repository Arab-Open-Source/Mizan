package com.eec.app.android

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.eec.app.App
import com.eec.core.db.AppDatabase
import com.eec.core.db.createDatabase
import com.eec.core.share.AndroidShareContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidShareContext.initialize(applicationContext)
        val database = AppGraph.database(this)
        setContent {
            App(database)
        }
    }
}

private object AppGraph {
    private var database: AppDatabase? = null

    fun database(context: Context): AppDatabase =
        database ?: createDatabase(context.applicationContext).also { database = it }
}

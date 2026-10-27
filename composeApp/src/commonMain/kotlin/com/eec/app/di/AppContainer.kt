package com.eec.app.di

import com.eec.core.db.AppDatabase
import com.eec.core.db.CategorySeeder
import com.eec.core.repository.CategoryRepository
import com.eec.core.repository.SettingsRepository
import com.eec.core.repository.TransactionRepository
import com.eec.core.share.ShareService
import com.eec.core.time.Clock
import com.eec.core.time.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppContainer(database: AppDatabase) {
    val clock: Clock = SystemClock
    val categories: CategoryRepository = CategoryRepository(database)
    val transactions: TransactionRepository = TransactionRepository(database, clock)
    val settings: SettingsRepository = SettingsRepository(database)
    val shareService: ShareService = ShareService()

    private val seeder = CategorySeeder(database)

    suspend fun prepare() {
        seeder.seed()
    }

    fun createScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}

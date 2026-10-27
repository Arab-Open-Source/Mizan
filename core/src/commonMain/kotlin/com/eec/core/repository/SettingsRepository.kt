package com.eec.core.repository

import com.eec.core.db.AppDatabase
import com.eec.core.db.SettingEntity
import kotlinx.coroutines.CancellationException

enum class SettingKey(val storageKey: String) {
    THEME("theme"),
    DEFAULT_PAYMENT_METHOD("default_payment_method"),
    LAST_SELECTED_MONTH("last_selected_month"),
    ONBOARDING("onboarding"),
    APP_LOCK("app_lock"),
}

class SettingsRepository(database: AppDatabase) {
    private val settingDao = database.settingDao()

    suspend fun get(key: SettingKey): String? = settingDao.value(key.storageKey)

    suspend fun put(key: SettingKey, value: String): WriteResult<Unit> = try {
        settingDao.put(SettingEntity(key = key.storageKey, value = value))
        WriteResult.Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        WriteResult.Failure(RepositoryError.Storage(error))
    }

    suspend fun remove(key: SettingKey): WriteResult<Unit> = try {
        settingDao.remove(key.storageKey)
        WriteResult.Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        WriteResult.Failure(RepositoryError.Storage(error))
    }
}

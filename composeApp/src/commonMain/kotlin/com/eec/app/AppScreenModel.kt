package com.eec.app

import com.eec.app.di.AppContainer
import com.eec.app.localization.LanguageMode
import com.eec.app.resources.Res
import com.eec.app.resources.error_storage
import com.eec.app.resources.pin_error_incorrect
import com.eec.app.resources.theme_save_failed
import com.eec.core.repository.SettingKey
import com.eec.core.repository.WriteResult
import com.eec.core.security.PinLock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    ;

    companion object {
        fun fromStorage(value: String?): ThemeMode =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}

data class AppState(
    val loading: Boolean = true,
    val error: String? = null,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val language: LanguageMode = LanguageMode.ARABIC,
    val message: StringResource? = null,
    val lockEnabled: Boolean = false,
    val locked: Boolean = false,
    val pinError: StringResource? = null,
)

sealed interface AppEvent {
    data object Start : AppEvent

    data class SetTheme(val theme: ThemeMode) : AppEvent

    data class SetLanguage(val language: LanguageMode) : AppEvent

    data class Unlock(val pin: String) : AppEvent

    data class LockChanged(val enabled: Boolean) : AppEvent

    data object MessageShown : AppEvent
}

class AppScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private var storedPinHash: String? = null

    fun onEvent(event: AppEvent) {
        when (event) {
            AppEvent.Start -> start()
            is AppEvent.SetTheme -> setTheme(event.theme)
            is AppEvent.SetLanguage -> setLanguage(event.language)
            is AppEvent.Unlock -> unlock(event.pin)
            is AppEvent.LockChanged -> lockChanged(event.enabled)
            AppEvent.MessageShown -> _state.value = _state.value.copy(message = null)
        }
    }

    private fun start() {
        scope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            try {
                container.prepare()
                val theme = ThemeMode.fromStorage(container.settings.get(SettingKey.THEME))
                val language = LanguageMode.fromStorage(container.settings.get(SettingKey.LANGUAGE))
                storedPinHash = container.settings.get(SettingKey.APP_LOCK)?.takeIf { it.isNotBlank() }
                val locked = storedPinHash != null
                _state.value = _state.value.copy(
                    loading = false,
                    theme = theme,
                    language = language,
                    lockEnabled = locked,
                    locked = locked,
                    pinError = null,
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = error.message ?: error.toString(),
                )
            }
        }
    }

    private fun lockChanged(enabled: Boolean) {
        if (!enabled) storedPinHash = null
        _state.value = _state.value.copy(
            lockEnabled = enabled,
            locked = if (enabled) _state.value.locked else false,
            pinError = if (enabled) _state.value.pinError else null,
        )
    }

    private fun unlock(pin: String) {
        val hash = storedPinHash
        if (hash == null) {
            _state.value = _state.value.copy(locked = false, pinError = null)
            return
        }
        scope.launch {
            val unlocked = PinLock.verify(pin, hash)
            _state.value = _state.value.copy(
                locked = !unlocked,
                pinError = if (unlocked) null else Res.string.pin_error_incorrect,
            )
        }
    }

    private fun setTheme(theme: ThemeMode) {
        if (theme == _state.value.theme) return
        val previous = _state.value.theme
        _state.value = _state.value.copy(theme = theme)
        scope.launch {
            when (val result = container.settings.put(SettingKey.THEME, theme.name)) {
                is WriteResult.Success -> Unit
                is WriteResult.Failure -> _state.value = _state.value.copy(
                    theme = previous,
                    message = Res.string.theme_save_failed,
                )
            }
        }
    }

    private fun setLanguage(language: LanguageMode) {
        if (language == _state.value.language) return
        val previous = _state.value.language
        _state.value = _state.value.copy(language = language)
        scope.launch {
            when (val result = container.settings.put(SettingKey.LANGUAGE, language.name)) {
                is WriteResult.Success -> Unit
                is WriteResult.Failure -> _state.value = _state.value.copy(
                    language = previous,
                    message = Res.string.error_storage,
                )
            }
        }
    }
}

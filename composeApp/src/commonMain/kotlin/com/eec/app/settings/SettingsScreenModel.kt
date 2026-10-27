package com.eec.app.settings

import com.eec.app.di.AppContainer
import com.eec.app.resources.Res
import com.eec.app.resources.csv_ready
import com.eec.app.resources.error_storage
import com.eec.app.resources.export_unavailable
import com.eec.app.resources.lock_changed_message
import com.eec.app.resources.lock_current_incorrect
import com.eec.app.resources.lock_disabled_message
import com.eec.app.resources.lock_enabled_message
import com.eec.app.resources.lock_length_error
import com.eec.app.resources.lock_mismatch
import com.eec.app.resources.lock_read_failed
import com.eec.app.share.toCsvBytes
import com.eec.app.ui.toUserMessage
import com.eec.core.report.CsvExporter
import com.eec.core.repository.SettingKey
import com.eec.core.repository.WriteResult
import com.eec.core.security.PinLock
import com.eec.core.time.toCairoDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

enum class LockMode {
    ENABLE,
    CHANGE,
    DISABLE,
}

data class LockDraft(
    val mode: LockMode,
    val currentPin: String = "",
    val newPin: String = "",
    val confirmPin: String = "",
    val error: StringResource? = null,
)

data class SettingsState(
    val exporting: Boolean = false,
    val message: StringResource? = null,
    val lockLoaded: Boolean = false,
    val lockEnabled: Boolean = false,
    val lockDraft: LockDraft? = null,
    val lockSaving: Boolean = false,
) {
    val canSaveLock: Boolean
        get() {
            val draft = lockDraft ?: return false
            if (lockSaving || !lockLoaded) return false
            val newPinValid = PinLock.isValidPin(draft.newPin) && draft.newPin == draft.confirmPin
            return when (draft.mode) {
                LockMode.ENABLE -> newPinValid
                LockMode.CHANGE -> draft.currentPin.isNotEmpty() && newPinValid
                LockMode.DISABLE -> draft.currentPin.isNotEmpty()
            }
        }
}

sealed interface SettingsEvent {
    data object ExportAll : SettingsEvent

    data class OpenLockDialog(val mode: LockMode) : SettingsEvent

    data object DismissLockDialog : SettingsEvent

    data class CurrentPinChanged(val pin: String) : SettingsEvent

    data class NewPinChanged(val pin: String) : SettingsEvent

    data class ConfirmPinChanged(val pin: String) : SettingsEvent

    data object SaveLock : SettingsEvent

    data object MessageShown : SettingsEvent
}

class SettingsScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
    private val onLockChanged: (Boolean) -> Unit,
) {
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private var storedPinHash: String? = null

    init {
        loadLock()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.ExportAll -> exportAll()
            is SettingsEvent.OpenLockDialog -> openLockDialog(event.mode)
            SettingsEvent.DismissLockDialog -> _state.update {
                it.copy(lockDraft = null, lockSaving = false)
            }

            is SettingsEvent.CurrentPinChanged -> updateDraft { it.copy(currentPin = normalizePin(event.pin)) }
            is SettingsEvent.NewPinChanged -> updateDraft { it.copy(newPin = normalizePin(event.pin)) }
            is SettingsEvent.ConfirmPinChanged -> updateDraft { it.copy(confirmPin = normalizePin(event.pin)) }
            SettingsEvent.SaveLock -> saveLock()
            SettingsEvent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    private fun loadLock() {
        scope.launch {
            try {
                storedPinHash = container.settings.get(SettingKey.APP_LOCK)?.takeIf { it.isNotBlank() }
                _state.update { it.copy(lockLoaded = true, lockEnabled = storedPinHash != null) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update { it.copy(lockLoaded = true, message = Res.string.lock_read_failed) }
            }
        }
    }

    private fun openLockDialog(mode: LockMode) {
        _state.update { it.copy(lockDraft = LockDraft(mode = mode)) }
    }

    private fun updateDraft(transform: (LockDraft) -> LockDraft) {
        _state.update { state ->
            val draft = state.lockDraft ?: return@update state
            state.copy(lockDraft = transform(draft).copy(error = null))
        }
    }

    private fun saveLock() {
        val draft = _state.value.lockDraft ?: return
        if (!_state.value.canSaveLock) return
        _state.update { it.copy(lockSaving = true, lockDraft = it.lockDraft?.copy(error = null)) }
        scope.launch {
            val error = applyLockDraft(draft)
            if (error != null) {
                _state.update { state ->
                    state.copy(lockSaving = false, lockDraft = state.lockDraft?.copy(error = error))
                }
                return@launch
            }
            val enabled = draft.mode != LockMode.DISABLE
            _state.update {
                it.copy(
                    lockSaving = false,
                    lockDraft = null,
                    lockEnabled = enabled,
                    message = when (draft.mode) {
                        LockMode.ENABLE -> Res.string.lock_enabled_message
                        LockMode.CHANGE -> Res.string.lock_changed_message
                        LockMode.DISABLE -> Res.string.lock_disabled_message
                    },
                )
            }
            onLockChanged(enabled)
        }
    }

    private suspend fun applyLockDraft(draft: LockDraft): StringResource? = when (draft.mode) {
        LockMode.ENABLE -> writeNewPin(draft)
        LockMode.CHANGE -> if (verifyStoredPin(draft.currentPin)) {
            writeNewPin(draft)
        } else {
            Res.string.lock_current_incorrect
        }

        LockMode.DISABLE -> if (verifyStoredPin(draft.currentPin)) {
            removePin()
        } else {
            Res.string.lock_current_incorrect
        }
    }

    private suspend fun writeNewPin(draft: LockDraft): StringResource? {
        if (!PinLock.isValidPin(draft.newPin)) return Res.string.lock_length_error
        if (draft.newPin != draft.confirmPin) return Res.string.lock_mismatch
        val hash = PinLock.hash(draft.newPin)
        return when (val result = container.settings.put(SettingKey.APP_LOCK, hash)) {
            is WriteResult.Success -> {
                storedPinHash = hash
                null
            }

            is WriteResult.Failure -> result.error.toUserMessage()
        }
    }

    private suspend fun removePin(): StringResource? =
        when (val result = container.settings.remove(SettingKey.APP_LOCK)) {
            is WriteResult.Success -> {
                storedPinHash = null
                null
            }

            is WriteResult.Failure -> result.error.toUserMessage()
        }

    private fun verifyStoredPin(pin: String): Boolean {
        val hash = storedPinHash ?: return false
        return PinLock.verify(pin, hash)
    }

    private fun normalizePin(value: String): String =
        value.filter { it.isDigit() }.take(PinLock.MAX_LENGTH)

    private fun exportAll() {
        if (_state.value.exporting) return
        scope.launch {
            _state.update { it.copy(exporting = true, message = null) }
            try {
                if (!container.shareService.canShareFiles()) {
                    _state.update { it.copy(exporting = false, message = Res.string.export_unavailable) }
                    return@launch
                }
                val rows = container.transactions.exportRows()
                val bytes = rows.toCsvBytes()
                val fileName = CsvExporter.fileName(container.clock.now().toCairoDate())
                container.shareService.shareFile(bytes, fileName, CSV_MIME_TYPE)
                _state.update { it.copy(exporting = false, message = Res.string.csv_ready) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _state.update { it.copy(exporting = false, message = Res.string.error_storage) }
            }
        }
    }

    private companion object {
        const val CSV_MIME_TYPE: String = "text/csv"
    }
}

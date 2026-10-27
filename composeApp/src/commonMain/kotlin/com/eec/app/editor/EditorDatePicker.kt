package com.eec.app.editor

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.action_cancel
import com.eec.app.resources.action_confirm
import com.eec.design.theme.EecTheme
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorDatePicker(
    initial: LocalDate,
    onSelect: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initial.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = pickerState.selectedDateMillis
                    if (millis != null) {
                        onSelect(
                            Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.UTC)
                                .date,
                        )
                    }
                    onDismiss()
                },
            ) {
                Text(text = appString(Res.string.action_confirm), style = EecTheme.typography.bodyStrong)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = appString(Res.string.action_cancel), style = EecTheme.typography.bodyStrong)
            }
        },
        shape = EecTheme.shapes.dialog,
    ) {
        DatePicker(state = pickerState)
    }
}

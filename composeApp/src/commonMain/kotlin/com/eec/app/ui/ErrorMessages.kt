package com.eec.app.ui

import com.eec.app.resources.Res
import com.eec.app.resources.amount_positive_error
import com.eec.app.resources.error_amount_required
import com.eec.app.resources.error_category_invalid
import com.eec.app.resources.error_category_name_required
import com.eec.app.resources.error_category_name_too_long
import com.eec.app.resources.error_color_format
import com.eec.app.resources.error_color_required
import com.eec.app.resources.error_date_invalid
import com.eec.app.resources.error_invalid_data
import com.eec.app.resources.error_note_too_long
import com.eec.app.resources.error_not_found
import com.eec.app.resources.error_storage
import com.eec.core.repository.RepositoryError
import com.eec.core.repository.ValidationField
import com.eec.core.repository.ValidationReason
import com.eec.core.repository.WriteResult
import com.eec.core.repository.errorOrNull
import org.jetbrains.compose.resources.StringResource

fun RepositoryError.toUserMessage(): StringResource = when (this) {
    is RepositoryError.Validation -> validationMessage(field, reason)
    is RepositoryError.NotFound -> Res.string.error_not_found
    is RepositoryError.Storage -> Res.string.error_storage
}

fun <T> WriteResult<T>.failureMessage(): StringResource? = errorOrNull()?.toUserMessage()

private fun validationMessage(field: ValidationField, reason: ValidationReason): StringResource = when {
    field == ValidationField.AMOUNT && reason == ValidationReason.MUST_BE_POSITIVE ->
        Res.string.amount_positive_error

    field == ValidationField.AMOUNT && reason == ValidationReason.REQUIRED -> Res.string.error_amount_required
    field == ValidationField.NOTE && reason == ValidationReason.TOO_LONG -> Res.string.error_note_too_long
    field == ValidationField.CATEGORY -> Res.string.error_category_invalid
    field == ValidationField.CATEGORY_NAME && reason == ValidationReason.REQUIRED ->
        Res.string.error_category_name_required

    field == ValidationField.CATEGORY_NAME && reason == ValidationReason.TOO_LONG ->
        Res.string.error_category_name_too_long

    field == ValidationField.CATEGORY_COLOR && reason == ValidationReason.INVALID_FORMAT ->
        Res.string.error_color_format

    field == ValidationField.CATEGORY_COLOR -> Res.string.error_color_required
    field == ValidationField.OCCURRED_AT -> Res.string.error_date_invalid
    else -> Res.string.error_invalid_data
}

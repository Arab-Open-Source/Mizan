package com.eec.app.ui

import com.eec.core.repository.RepositoryError
import com.eec.core.repository.ValidationField
import com.eec.core.repository.ValidationReason
import com.eec.core.repository.WriteResult
import com.eec.core.repository.errorOrNull

fun RepositoryError.toUserMessage(): String = when (this) {
    is RepositoryError.Validation -> validationMessage(field, reason)
    is RepositoryError.NotFound -> "العنصر المطلوب غير موجود"
    is RepositoryError.Storage -> "تعذر حفظ البيانات. حاول مرة أخرى."
}

fun <T> WriteResult<T>.failureMessage(): String? = errorOrNull()?.toUserMessage()

private fun validationMessage(field: ValidationField, reason: ValidationReason): String = when {
    field == ValidationField.AMOUNT && reason == ValidationReason.MUST_BE_POSITIVE ->
        "المبلغ يجب أن يكون أكبر من صفر"

    field == ValidationField.AMOUNT && reason == ValidationReason.REQUIRED -> "أدخل المبلغ"
    field == ValidationField.NOTE && reason == ValidationReason.TOO_LONG -> "الملاحظة أطول من 280 حرفًا"
    field == ValidationField.CATEGORY -> "اختر تصنيفًا صالحًا"
    field == ValidationField.CATEGORY_NAME && reason == ValidationReason.REQUIRED ->
        "أدخل اسم التصنيف"

    field == ValidationField.CATEGORY_NAME && reason == ValidationReason.TOO_LONG ->
        "اسم التصنيف أطول من 40 حرفًا"

    field == ValidationField.CATEGORY_COLOR && reason == ValidationReason.INVALID_FORMAT ->
        "أدخل لونًا بصيغة #RRGGBB"

    field == ValidationField.CATEGORY_COLOR -> "اختر لونًا للتصنيف"
    field == ValidationField.OCCURRED_AT -> "التاريخ غير صالح"
    else -> "البيانات غير صالحة"
}

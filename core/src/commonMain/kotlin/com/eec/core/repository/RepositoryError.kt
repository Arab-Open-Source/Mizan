package com.eec.core.repository

sealed interface RepositoryError {
    data class Validation(
        val field: ValidationField,
        val reason: ValidationReason,
    ) : RepositoryError

    data class NotFound(val id: String) : RepositoryError

    data class Storage(val cause: Throwable) : RepositoryError
}

enum class ValidationField {
    AMOUNT,
    NOTE,
    CATEGORY,
    OCCURRED_AT,
    PAYMENT_METHOD,
    KIND,
    SETTING_KEY,
    SETTING_VALUE,
    CATEGORY_NAME,
    CATEGORY_COLOR,
}

enum class ValidationReason {
    REQUIRED,
    MUST_BE_POSITIVE,
    TOO_LONG,
    NOT_FOUND,
    INVALID_FORMAT,
}

sealed interface WriteResult<out T> {
    data class Success<T>(val value: T) : WriteResult<T>
    data class Failure(val error: RepositoryError) : WriteResult<Nothing>
}

fun <T> WriteResult<T>.valueOrNull(): T? = when (this) {
    is WriteResult.Success -> value
    is WriteResult.Failure -> null
}

fun <T> WriteResult<T>.errorOrNull(): RepositoryError? = when (this) {
    is WriteResult.Success -> null
    is WriteResult.Failure -> error
}

package com.eec.core.model

enum class TransactionKind {
    EXPENSE,
    INCOME,
    ;

    companion object {
        fun fromStorage(value: String): TransactionKind =
            entries.firstOrNull { it.name == value }
                ?: throw IllegalArgumentException("Unknown transaction kind: $value")
    }
}

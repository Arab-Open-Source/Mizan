package com.eec.core.model

enum class PaymentMethod {
    CASH,
    CARD,
    WALLET,
    TRANSFER,
    OTHER,
    ;

    companion object {
        fun fromStorage(value: String): PaymentMethod =
            entries.firstOrNull { it.name == value }
                ?: throw IllegalArgumentException("Unknown payment method: $value")
    }
}

package com.eec.core.model

import kotlin.time.Instant

data class Transaction(
    val id: String,
    val kind: TransactionKind,
    val amount: Money,
    val currency: String,
    val categoryId: String,
    val occurredAt: Instant,
    val note: String?,
    val paymentMethod: PaymentMethod,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class TransactionListItem(
    val id: String,
    val kind: TransactionKind,
    val amount: Money,
    val currency: String,
    val categoryId: String,
    val categoryName: String,
    val categoryColorKey: String,
    val categoryIconKey: String,
    val occurredAt: Instant,
    val note: String?,
    val paymentMethod: PaymentMethod,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class NewTransaction(
    val kind: TransactionKind,
    val amount: Money,
    val categoryId: String,
    val occurredAt: Instant,
    val note: String?,
    val paymentMethod: PaymentMethod,
)

data class TransactionFilter(
    val startInclusive: Instant? = null,
    val endExclusive: Instant? = null,
    val kind: TransactionKind? = null,
    val categoryId: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val noteQuery: String? = null,
)

data class Category(
    val id: String,
    val name: String,
    val kind: TransactionKind,
    val colorKey: String,
    val iconKey: String,
    val sortOrder: Int,
    val archived: Boolean,
)

data class MonthTotals(
    val expense: Money,
    val income: Money,
) {
    val net: Money get() = income - expense
}

data class CategoryTotal(
    val categoryId: String,
    val name: String,
    val colorKey: String,
    val iconKey: String,
    val amount: Money,
)

data class DailyTotal(
    val date: kotlinx.datetime.LocalDate,
    val amount: Money,
)

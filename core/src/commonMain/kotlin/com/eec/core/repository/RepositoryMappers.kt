package com.eec.core.repository

import com.eec.core.db.CategoryEntity
import com.eec.core.db.CategoryTotalRow
import com.eec.core.db.TransactionEntity
import com.eec.core.db.TransactionWithCategoryRow
import com.eec.core.model.Category
import com.eec.core.model.CategoryTotal
import com.eec.core.model.Money
import com.eec.core.model.PaymentMethod
import com.eec.core.model.Transaction
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import kotlin.time.Instant

internal fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    kind = TransactionKind.fromStorage(kind),
    colorKey = colorKey,
    iconKey = iconKey,
    sortOrder = sortOrder,
    archived = archived,
)

internal fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    kind = TransactionKind.fromStorage(kind),
    amount = Money.ofMinor(amountMinor),
    currency = currency,
    categoryId = categoryId,
    occurredAt = Instant.fromEpochMilliseconds(occurredAt),
    note = note,
    paymentMethod = PaymentMethod.fromStorage(paymentMethod),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
)

internal fun TransactionWithCategoryRow.toDomain(): TransactionListItem = TransactionListItem(
    id = id,
    kind = TransactionKind.fromStorage(kind),
    amount = Money.ofMinor(amountMinor),
    currency = currency,
    categoryId = categoryId,
    categoryName = categoryName,
    categoryColorKey = categoryColorKey,
    categoryIconKey = categoryIconKey,
    occurredAt = Instant.fromEpochMilliseconds(occurredAt),
    note = note,
    paymentMethod = PaymentMethod.fromStorage(paymentMethod),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt),
)

internal fun CategoryTotalRow.toDomain(): CategoryTotal = CategoryTotal(
    categoryId = categoryId,
    name = name,
    colorKey = colorKey,
    iconKey = iconKey,
    amount = Money.ofMinor(totalMinor),
)

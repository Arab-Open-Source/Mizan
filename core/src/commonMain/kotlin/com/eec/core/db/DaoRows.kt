package com.eec.core.db

import androidx.room3.ColumnInfo

data class TransactionWithCategoryRow(
    val id: String,
    val kind: String,
    @ColumnInfo(name = "amount_minor") val amountMinor: Long,
    val currency: String,
    @ColumnInfo(name = "category_id") val categoryId: String,
    @ColumnInfo(name = "category_name") val categoryName: String,
    @ColumnInfo(name = "category_color_key") val categoryColorKey: String,
    @ColumnInfo(name = "category_icon_key") val categoryIconKey: String,
    @ColumnInfo(name = "occurred_at") val occurredAt: Long,
    val note: String?,
    @ColumnInfo(name = "payment_method") val paymentMethod: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
)

data class KindTotalRow(
    val kind: String,
    @ColumnInfo(name = "total_minor") val totalMinor: Long,
)

data class CategoryTotalRow(
    @ColumnInfo(name = "category_id") val categoryId: String,
    val name: String,
    @ColumnInfo(name = "color_key") val colorKey: String,
    @ColumnInfo(name = "icon_key") val iconKey: String,
    @ColumnInfo(name = "total_minor") val totalMinor: Long,
)

data class DailyTotalRow(
    val day: String,
    @ColumnInfo(name = "total_minor") val totalMinor: Long,
)

data class DailyBucketRow(
    @ColumnInfo(name = "day_bucket") val dayBucket: Long,
    @ColumnInfo(name = "total_minor") val totalMinor: Long,
)

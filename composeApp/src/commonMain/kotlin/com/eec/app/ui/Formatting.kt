package com.eec.app.ui

import androidx.compose.ui.graphics.vector.ImageVector
import com.eec.core.format.AmountFormatter
import com.eec.core.model.MonthPeriod
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.time.toCairoDate
import com.eec.design.icon.EecIcons
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

private val monthNames: List<String> = listOf(
    "يناير",
    "فبراير",
    "مارس",
    "أبريل",
    "مايو",
    "يونيو",
    "يوليو",
    "أغسطس",
    "سبتمبر",
    "أكتوبر",
    "نوفمبر",
    "ديسمبر",
)

fun monthName(monthNumber: Int): String = monthNames[monthNumber - 1]

fun monthTitle(period: MonthPeriod): String = "${monthName(period.monthNumber)} ${period.year}"

fun weekdayName(date: LocalDate): String = when (date.dayOfWeek.ordinal) {
    0 -> "الاثنين"
    1 -> "الثلاثاء"
    2 -> "الأربعاء"
    3 -> "الخميس"
    4 -> "الجمعة"
    5 -> "السبت"
    else -> "الأحد"
}

fun dayLabel(date: LocalDate): String = "${weekdayName(date)} ${date.day} ${monthName(date.month.number)}"

fun shortDateLabel(date: LocalDate): String = "${date.day} ${monthName(date.month.number)} ${date.year}"

fun paymentMethodLabel(paymentMethod: PaymentMethod): String = when (paymentMethod) {
    PaymentMethod.CASH -> "نقدي"
    PaymentMethod.CARD -> "بطاقة"
    PaymentMethod.WALLET -> "محفظة"
    PaymentMethod.TRANSFER -> "تحويل"
    PaymentMethod.OTHER -> "أخرى"
}

fun paymentMethodIcon(paymentMethod: PaymentMethod): ImageVector = when (paymentMethod) {
    PaymentMethod.CASH -> EecIcons.cash
    PaymentMethod.CARD -> EecIcons.card
    PaymentMethod.WALLET -> EecIcons.wallet
    PaymentMethod.TRANSFER -> EecIcons.transfer
    PaymentMethod.OTHER -> EecIcons.note
}

fun transactionKindLabel(kind: TransactionKind): String = when (kind) {
    TransactionKind.EXPENSE -> "مصروف"
    TransactionKind.INCOME -> "دخل"
}

fun transactionSubtitle(item: TransactionListItem): String =
    "${item.categoryName} · ${paymentMethodLabel(item.paymentMethod)}"

fun transactionDate(item: TransactionListItem): LocalDate = item.occurredAt.toCairoDate()

fun moneyText(minor: Long): String = AmountFormatter.format(minor)

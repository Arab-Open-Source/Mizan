package com.eec.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.month_1
import com.eec.app.resources.month_10
import com.eec.app.resources.month_11
import com.eec.app.resources.month_12
import com.eec.app.resources.month_2
import com.eec.app.resources.month_3
import com.eec.app.resources.month_4
import com.eec.app.resources.month_5
import com.eec.app.resources.month_6
import com.eec.app.resources.month_7
import com.eec.app.resources.month_8
import com.eec.app.resources.month_9
import com.eec.app.resources.payment_card
import com.eec.app.resources.payment_cash
import com.eec.app.resources.payment_other
import com.eec.app.resources.payment_transfer
import com.eec.app.resources.payment_wallet
import com.eec.app.resources.kind_expense
import com.eec.app.resources.kind_income
import com.eec.app.resources.weekday_1
import com.eec.app.resources.weekday_2
import com.eec.app.resources.weekday_3
import com.eec.app.resources.weekday_4
import com.eec.app.resources.weekday_5
import com.eec.app.resources.weekday_6
import com.eec.app.resources.weekday_7
import com.eec.core.format.AmountFormatter
import com.eec.core.model.MonthPeriod
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.time.toCairoDate
import com.eec.design.icon.EecIcons
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import org.jetbrains.compose.resources.StringResource

val monthResources: List<StringResource> = listOf(
    Res.string.month_1,
    Res.string.month_2,
    Res.string.month_3,
    Res.string.month_4,
    Res.string.month_5,
    Res.string.month_6,
    Res.string.month_7,
    Res.string.month_8,
    Res.string.month_9,
    Res.string.month_10,
    Res.string.month_11,
    Res.string.month_12,
)

private val weekdayResources: List<StringResource> = listOf(
    Res.string.weekday_1,
    Res.string.weekday_2,
    Res.string.weekday_3,
    Res.string.weekday_4,
    Res.string.weekday_5,
    Res.string.weekday_6,
    Res.string.weekday_7,
)

@Composable
fun monthName(monthNumber: Int): String = appString(monthResources[monthNumber - 1])

@Composable
fun monthTitle(period: MonthPeriod): String = "${monthName(period.monthNumber)} ${period.year}"

@Composable
fun weekdayName(date: LocalDate): String = appString(weekdayResources[date.dayOfWeek.ordinal])

@Composable
fun dayLabel(date: LocalDate): String = "${weekdayName(date)} ${date.day} ${monthName(date.month.number)}"

@Composable
fun shortDateLabel(date: LocalDate): String = "${date.day} ${monthName(date.month.number)} ${date.year}"

@Composable
fun paymentMethodLabel(paymentMethod: PaymentMethod): String = appString(
    when (paymentMethod) {
        PaymentMethod.CASH -> Res.string.payment_cash
        PaymentMethod.CARD -> Res.string.payment_card
        PaymentMethod.WALLET -> Res.string.payment_wallet
        PaymentMethod.TRANSFER -> Res.string.payment_transfer
        PaymentMethod.OTHER -> Res.string.payment_other
    },
)

fun paymentMethodIcon(paymentMethod: PaymentMethod): ImageVector = when (paymentMethod) {
    PaymentMethod.CASH -> EecIcons.cash
    PaymentMethod.CARD -> EecIcons.card
    PaymentMethod.WALLET -> EecIcons.wallet
    PaymentMethod.TRANSFER -> EecIcons.transfer
    PaymentMethod.OTHER -> EecIcons.note
}

@Composable
fun transactionKindLabel(kind: TransactionKind): String = appString(
    when (kind) {
        TransactionKind.EXPENSE -> Res.string.kind_expense
        TransactionKind.INCOME -> Res.string.kind_income
    },
)

@Composable
fun transactionSubtitle(item: TransactionListItem): String =
    "${item.categoryName} · ${paymentMethodLabel(item.paymentMethod)}"

fun transactionDate(item: TransactionListItem): LocalDate = item.occurredAt.toCairoDate()

fun moneyText(minor: Long): String = AmountFormatter.format(minor)

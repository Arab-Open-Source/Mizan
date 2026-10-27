package com.eec.app.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.app.ui.MaxWidthContent
import com.eec.app.ui.dayLabel
import com.eec.app.ui.moneyText
import com.eec.app.ui.transactionDate
import com.eec.app.ui.transactionSubtitle
import com.eec.core.format.AmountFormatter
import com.eec.core.model.Money
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.design.component.AmountSign
import com.eec.design.component.ConfirmDialog
import com.eec.design.component.EecTextField
import com.eec.design.component.EmptyState
import com.eec.design.component.ErrorState
import com.eec.design.component.LoadingState
import com.eec.design.component.TransactionRow
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecTheme
import kotlinx.datetime.LocalDate

@Composable
fun TransactionsScreen(
    state: TransactionsState,
    onEvent: (TransactionsEvent) -> Unit,
    onEdit: (TransactionListItem) -> Unit,
    onAdd: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(TransactionsEvent.MessageShown)
    }

    MaxWidthContent(modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            TransactionControls(state = state, onEvent = onEvent)
            when {
                state.loading && state.items.isEmpty() -> LoadingState(message = "جارٍ تحميل المعاملات")
                state.error != null && state.items.isEmpty() -> ErrorState(
                    title = "تعذر تحميل المعاملات",
                    message = state.error,
                    onRetry = { onEvent(TransactionsEvent.Retry) },
                )

                state.items.isEmpty() -> EmptyState(
                    title = if (state.hasFilters) "لا توجد نتائج" else "لا توجد معاملات",
                    message = if (state.hasFilters) {
                        "جرّب تغيير البحث أو التصفية."
                    } else {
                        "أضف أول معاملة لتظهر هنا."
                    },
                    icon = EecIcons.transactions,
                    actionLabel = if (state.hasFilters) null else "إضافة معاملة",
                    onAction = if (state.hasFilters) null else onAdd,
                )

                else -> TransactionList(state = state, onEvent = onEvent, onEdit = onEdit)
            }
        }
    }

    state.pendingDelete?.let { target ->
        ConfirmDialog(
            title = "حذف المعاملة",
            message = "سيتم حذف ${target.categoryName} بمبلغ ${AmountFormatter.format(target.amount.minor)}. لا يمكن التراجع.",
            confirmLabel = "حذف",
            dismissLabel = "إلغاء",
            destructive = true,
            onConfirm = { onEvent(TransactionsEvent.ConfirmDelete) },
            onDismiss = { onEvent(TransactionsEvent.CancelDelete) },
        )
    }

    TransactionFilterSheet(state = state, onEvent = onEvent)
}

@Composable
private fun TransactionControls(
    state: TransactionsState,
    onEvent: (TransactionsEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = EecTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EecTextField(
                value = state.search,
                onValueChange = { onEvent(TransactionsEvent.SearchChanged(it)) },
                label = "بحث",
                placeholder = "ملاحظة أو تصنيف",
                leadingIcon = EecIcons.search,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { onEvent(TransactionsEvent.ShowFilters) }) {
                Icon(
                    imageVector = EecIcons.filter,
                    contentDescription = "تصفية المعاملات",
                    tint = EecTheme.colors.onBackground,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SummaryItem(label = "المصروف", value = moneyText(state.expenseTotal.minor))
            SummaryItem(label = "الدخل", value = moneyText(state.incomeTotal.minor))
            SummaryItem(label = "العدد", value = state.items.size.toString())
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = EecTheme.typography.caption,
            color = EecTheme.colors.onSurfaceVariant,
        )
        Text(
            text = value,
            style = EecTheme.typography.bodyStrong,
            color = EecTheme.colors.onBackground,
            maxLines = 1,
        )
    }
}

@Composable
private fun TransactionList(
    state: TransactionsState,
    onEvent: (TransactionsEvent) -> Unit,
    onEdit: (TransactionListItem) -> Unit,
) {
    val groups = state.items.groupBy { transactionDate(it) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = EecTheme.spacing.screen,
            end = EecTheme.spacing.screen,
            top = EecTheme.spacing.xs,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
    ) {
        groups.forEach { (date, items) ->
            item(key = "day-${date}") { DayHeader(date = date, items = items) }
            items(items.size, key = { index -> items[index].id }) { index ->
                val item = items[index]
                TransactionEntry(
                    item = item,
                    onEdit = { onEdit(item) },
                    onDelete = { onEvent(TransactionsEvent.RequestDelete(item)) },
                )
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate, items: List<TransactionListItem>) {
    val expense = Money.total(
        items.filter { it.kind == TransactionKind.EXPENSE }.map { it.amount },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = EecTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = dayLabel(date),
            style = EecTheme.typography.label,
            color = EecTheme.colors.onSurfaceVariant,
        )
        Text(
            text = moneyText(expense.minor),
            style = EecTheme.typography.label,
            color = EecTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun TransactionEntry(
    item: TransactionListItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TransactionRow(
            title = item.categoryName,
            subtitle = transactionSubtitle(item),
            amountText = AmountFormatter.format(item.amount.minor),
            amountSign = when (item.kind) {
                TransactionKind.EXPENSE -> AmountSign.Negative
                TransactionKind.INCOME -> AmountSign.Positive
            },
            note = item.note,
            onClick = onEdit,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = EecIcons.delete,
                contentDescription = "حذف المعاملة",
                tint = EecTheme.colors.onSurfaceVariant,
            )
        }
    }
}

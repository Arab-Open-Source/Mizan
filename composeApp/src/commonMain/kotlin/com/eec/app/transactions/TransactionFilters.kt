package com.eec.app.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.filter_all
import com.eec.app.resources.filter_category
import com.eec.app.resources.filter_clear
import com.eec.app.resources.filter_kind
import com.eec.app.resources.filter_payment
import com.eec.app.resources.filter_transactions
import com.eec.app.ui.paymentMethodLabel
import com.eec.app.ui.transactionKindLabel
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import com.eec.design.component.CategoryChip
import com.eec.design.component.FilterSheet
import com.eec.design.component.SecondaryButton
import com.eec.design.theme.EecTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionFilterSheet(
    state: TransactionsState,
    onEvent: (TransactionsEvent) -> Unit,
) {
    FilterSheet(
        visible = state.filtersVisible,
        onDismiss = { onEvent(TransactionsEvent.HideFilters) },
        title = appString(Res.string.filter_transactions),
    ) {
        FilterGroupTitle(text = appString(Res.string.filter_kind))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            CategoryChip(
                label = appString(Res.string.filter_all),
                color = EecTheme.colors.primary,
                selected = state.kind == null,
                onClick = { onEvent(TransactionsEvent.KindChanged(null)) },
            )
            TransactionKind.entries.forEach { kind ->
                CategoryChip(
                    label = transactionKindLabel(kind),
                    color = EecTheme.colors.primary,
                    selected = state.kind == kind,
                    onClick = { onEvent(TransactionsEvent.KindChanged(kind)) },
                )
            }
        }

        FilterGroupTitle(text = appString(Res.string.filter_category))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            CategoryChip(
                label = appString(Res.string.filter_all),
                color = EecTheme.colors.primary,
                selected = state.categoryId == null,
                onClick = { onEvent(TransactionsEvent.CategoryChanged(null)) },
            )
            state.categories
                .filter { state.kind == null || it.kind == state.kind }
                .forEach { category ->
                    CategoryChip(
                        label = category.name,
                        color = EecTheme.colors.categoryPalette.colorFor(category.colorKey),
                        selected = state.categoryId == category.id,
                        onClick = { onEvent(TransactionsEvent.CategoryChanged(category.id)) },
                    )
                }
        }

        FilterGroupTitle(text = appString(Res.string.filter_payment))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            CategoryChip(
                label = appString(Res.string.filter_all),
                color = EecTheme.colors.primary,
                selected = state.paymentMethod == null,
                onClick = { onEvent(TransactionsEvent.PaymentChanged(null)) },
            )
            PaymentMethod.entries.forEach { method ->
                CategoryChip(
                    label = paymentMethodLabel(method),
                    color = EecTheme.colors.primary,
                    selected = state.paymentMethod == method,
                    onClick = { onEvent(TransactionsEvent.PaymentChanged(method)) },
                )
            }
        }

        if (state.hasFilters) {
            SecondaryButton(
                text = appString(Res.string.filter_clear),
                onClick = { onEvent(TransactionsEvent.ClearFilters) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun FilterGroupTitle(text: String) {
    Column {
        Text(
            text = text,
            style = EecTheme.typography.label,
            color = EecTheme.colors.onSurfaceVariant,
        )
    }
}

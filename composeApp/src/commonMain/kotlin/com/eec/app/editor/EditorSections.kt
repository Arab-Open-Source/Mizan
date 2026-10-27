package com.eec.app.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eec.app.ui.paymentMethodIcon
import com.eec.app.ui.paymentMethodLabel
import com.eec.app.ui.shortDateLabel
import com.eec.core.model.Category
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import com.eec.design.component.CategoryChip
import com.eec.design.component.CategoryGrid
import com.eec.design.component.CategoryGridItem
import com.eec.design.component.DateField
import com.eec.design.component.EecTextField
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun KindToggle(
    kind: TransactionKind,
    onKind: (TransactionKind) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
    ) {
        KindOption(
            label = "مصروف",
            selected = kind == TransactionKind.EXPENSE,
            color = EecTheme.colors.tertiary,
            onClick = { onKind(TransactionKind.EXPENSE) },
            modifier = Modifier.weight(1f),
        )
        KindOption(
            label = "دخل",
            selected = kind == TransactionKind.INCOME,
            color = EecTheme.colors.success,
            onClick = { onKind(TransactionKind.INCOME) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun KindOption(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = EecSizes.minTouchTarget),
        onClick = onClick,
        shape = EecTheme.shapes.chip,
        color = if (selected) color.copy(alpha = 0.14f) else EecTheme.colors.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) color else EecTheme.colors.outline,
        ),
    ) {
        Row(
            modifier = Modifier.padding(vertical = EecTheme.spacing.sm),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                style = EecTheme.typography.bodyStrong,
                color = EecTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun CategorySection(
    categories: List<Category>,
    selectedId: String?,
    onSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
        Text(
            text = "التصنيف",
            style = EecTheme.typography.label,
            color = EecTheme.colors.onSurfaceVariant,
        )
        if (categories.isEmpty()) {
            Text(
                text = "لا توجد تصنيفات متاحة لهذا النوع.",
                style = EecTheme.typography.caption,
                color = EecTheme.colors.onSurfaceVariant,
            )
        } else {
            CategoryGrid(
                items = categories.map { category ->
                    CategoryGridItem(
                        id = category.id,
                        name = category.name,
                        color = EecTheme.colors.categoryPalette.colorFor(category.colorKey),
                        icon = EecIcons.categoryIcon(category.iconKey),
                    )
                },
                selectedId = selectedId,
                onSelect = onSelect,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailsSection(
    state: TransactionEditorState,
    onEvent: (TransactionEditorEvent) -> Unit,
) {
    var datePickerVisible by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
        DateField(
            value = shortDateLabel(state.date),
            onClick = { datePickerVisible = true },
        )
        Text(
            text = "طريقة الدفع",
            style = EecTheme.typography.label,
            color = EecTheme.colors.onSurfaceVariant,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            PaymentMethod.entries.forEach { method ->
                CategoryChip(
                    label = paymentMethodLabel(method),
                    color = EecTheme.colors.primary,
                    selected = state.paymentMethod == method,
                    onClick = { onEvent(TransactionEditorEvent.PaymentChanged(method)) },
                    icon = paymentMethodIcon(method),
                )
            }
        }
        EecTextField(
            value = state.note,
            onValueChange = { onEvent(TransactionEditorEvent.NoteChanged(it)) },
            label = "ملاحظة",
            singleLine = false,
            isError = !state.noteValid,
            supportingText = "${state.note.length}/$NOTE_MAX_LENGTH",
        )
    }
    if (datePickerVisible) {
        EditorDatePicker(
            initial = state.date,
            onSelect = { onEvent(TransactionEditorEvent.DateChanged(it)) },
            onDismiss = { datePickerVisible = false },
        )
    }
}

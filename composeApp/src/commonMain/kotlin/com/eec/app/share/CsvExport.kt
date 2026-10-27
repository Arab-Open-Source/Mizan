package com.eec.app.share

import com.eec.core.model.TransactionListItem
import com.eec.core.report.CsvExporter
import com.eec.core.report.CsvRow

fun List<TransactionListItem>.toCsvBytes(): ByteArray = CsvExporter.build(
    rows = map { item ->
        CsvRow(
            occurredAt = item.occurredAt,
            kind = item.kind,
            categoryName = item.categoryName,
            amount = item.amount,
            paymentMethod = item.paymentMethod,
            note = item.note,
        )
    },
)

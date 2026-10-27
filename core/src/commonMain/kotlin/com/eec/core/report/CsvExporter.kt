package com.eec.core.report

import com.eec.core.format.AmountFormatter
import com.eec.core.model.Money
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import com.eec.core.time.CairoTimeZone
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class CsvRow(
    val occurredAt: Instant,
    val kind: TransactionKind,
    val categoryName: String,
    val amount: Money,
    val paymentMethod: PaymentMethod,
    val note: String?,
)

object CsvExporter {
    private const val BOM: String = "\uFEFF"
    private const val LINE_END: String = "\r\n"
    private const val HEADER: String = "date,kind,category,amount_egp,payment_method,note"

    fun build(
        rows: List<CsvRow>,
        timeZone: TimeZone = CairoTimeZone,
    ): ByteArray {
        val content = buildString {
            append(BOM)
            append(HEADER)
            append(LINE_END)
            rows.forEach { row ->
                append(field(row.occurredAt.toLocalDateTime(timeZone).date.toString()))
                append(',')
                append(field(row.kind.name))
                append(',')
                append(field(row.categoryName))
                append(',')
                append(field(AmountFormatter.formatCsv(row.amount.minor)))
                append(',')
                append(field(row.paymentMethod.name))
                append(',')
                append(field(row.note.orEmpty()))
                append(LINE_END)
            }
        }
        return content.encodeToByteArray()
    }

    fun fileName(date: LocalDate): String = "eec-transactions-$date.csv"

    private fun field(value: String): String {
        val requiresQuotes = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        return if (requiresQuotes) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }
}

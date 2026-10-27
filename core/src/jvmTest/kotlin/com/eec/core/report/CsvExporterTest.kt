package com.eec.core.report

import com.eec.core.model.Money
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant

class CsvExporterTest {
    @Test
    fun writesBomHeaderCrlfAndRfc4180Quoting() {
        val first = LocalDateTime(2026, 3, 15, 10, 30).toInstant(com.eec.core.time.CairoTimeZone)
        val second = LocalDateTime(2026, 3, 15, 18, 0).toInstant(com.eec.core.time.CairoTimeZone)

        val bytes = CsvExporter.build(
            rows = listOf(
                CsvRow(
                    occurredAt = first,
                    kind = TransactionKind.EXPENSE,
                    categoryName = "طعام, شرقي",
                    amount = Money.ofMinor(12345),
                    paymentMethod = PaymentMethod.CASH,
                    note = "قال \"مرحبا\",\nسطر",
                ),
                CsvRow(
                    occurredAt = second,
                    kind = TransactionKind.INCOME,
                    categoryName = "راتب",
                    amount = Money.ofMinor(500000),
                    paymentMethod = PaymentMethod.TRANSFER,
                    note = null,
                ),
            ),
        )

        val text = bytes.decodeToString()
        assertTrue(text.startsWith("\uFEFFdate,kind,category,amount_egp,payment_method,note\r\n"))
        assertEquals(3, text.windowed(2).count { it == "\r\n" })
        assertTrue(
            text.contains(
                "2026-03-15,EXPENSE,\"طعام, شرقي\",123.45,CASH,\"قال \"\"مرحبا\"\",\nسطر\"\r\n",
            ),
        )
        assertTrue(text.contains("2026-03-15,INCOME,راتب,5000.00,TRANSFER,\r\n"))
        assertEquals(0xEF.toByte(), bytes[0])
        assertEquals(0xBB.toByte(), bytes[1])
        assertEquals(0xBF.toByte(), bytes[2])
    }

    @Test
    fun fileNamesUseIsoDate() {
        assertEquals("eec-transactions-2026-03-15.csv", CsvExporter.fileName(LocalDate(2026, 3, 15)))
    }
}

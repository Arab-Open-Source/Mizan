package com.eec.core.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AmountFormatterTest {
    @Test
    fun formatsGroupedEgpWithTwoDecimals() {
        assertEquals("0.00", AmountFormatter.format(0))
        assertEquals("0.05", AmountFormatter.format(5))
        assertEquals("12.00", AmountFormatter.format(1200))
        assertEquals("1,234.56", AmountFormatter.format(123456))
        assertEquals("1,000,000.00", AmountFormatter.format(100000000))
        assertEquals("-1,234.56", AmountFormatter.format(-123456))
    }

    @Test
    fun formatsCsvWithoutGroupingOrSymbol() {
        assertEquals("0.00", AmountFormatter.formatCsv(0))
        assertEquals("1234.56", AmountFormatter.formatCsv(123456))
        assertEquals("-99.50", AmountFormatter.formatCsv(-9950))
    }

    @Test
    fun formatsWithCurrency() {
        assertEquals("1,234.56 ج.م", AmountFormatter.formatWithCurrency(123456))
    }

    @Test
    fun parsesUserInputToMinorUnits() {
        assertEquals(12345L, AmountFormatter.parseMinor("123.45"))
        assertNull(AmountFormatter.parseMinor(""))
    }
}

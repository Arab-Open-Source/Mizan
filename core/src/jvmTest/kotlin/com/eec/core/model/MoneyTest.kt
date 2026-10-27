package com.eec.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MoneyTest {
    @Test
    fun parsesDecimalEgpToMinorUnits() {
        assertEquals(12345L, Money.parse("123.45")?.minor)
        assertEquals(1200L, Money.parse("12")?.minor)
        assertEquals(50L, Money.parse("0.5")?.minor)
        assertEquals(5L, Money.parse("0.05")?.minor)
        assertEquals(-310L, Money.parse("-3.10")?.minor)
        assertEquals(0L, Money.parse("-0.00")?.minor)
    }

    @Test
    fun rejectsMalformedAmounts() {
        assertNull(Money.parse(""))
        assertNull(Money.parse("abc"))
        assertNull(Money.parse("1.234"))
        assertNull(Money.parse("1.2.3"))
        assertNull(Money.parse("-"))
        assertNull(Money.parse("12,5"))
    }

    @Test
    fun arithmeticStaysInMinorUnits() {
        val first = Money.ofEgp(12)
        val second = Money.ofMinor(345)
        assertEquals(1545L, (first + second).minor)
        assertEquals(855L, (first - second).minor)
        assertEquals(2400L, (first * 2).minor)
        assertEquals(-345L, (-second).minor)
        assertEquals(345L, (-second).absolute().minor)
    }

    @Test
    fun totalsAndComparison() {
        val values = listOf(Money.ofMinor(100), Money.ofMinor(-30), Money.ofMinor(5))
        assertEquals(75L, Money.total(values).minor)
        assertEquals(0L, Money.Zero.minor)
        assertTrue(Money.ofMinor(1) > Money.Zero)
        assertTrue(Money.ofMinor(-1).isNegative)
        assertTrue(Money.ofMinor(1).isPositive)
        assertTrue(Money.Zero.isZero)
    }
}

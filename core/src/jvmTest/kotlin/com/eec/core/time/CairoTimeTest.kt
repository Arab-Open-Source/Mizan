package com.eec.core.time

import com.eec.core.model.MonthPeriod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus

class CairoTimeTest {
    @Test
    fun monthStartsAtCairoMidnight() {
        val period = MonthPeriod(year = 2026, monthNumber = 3)
        assertEquals(LocalDateTime(2026, 3, 1, 0, 0), period.startInstant().toCairoDateTime())
        assertEquals(LocalDateTime(2026, 4, 1, 0, 0), period.endExclusiveInstant().toCairoDateTime())
        assertEquals(LocalDate(2026, 3, 1), period.startInstant().toCairoDate())
        assertEquals(
            LocalDate(2026, 3, 31),
            period.endExclusiveInstant().toCairoDate().minus(1, kotlinx.datetime.DateTimeUnit.DAY),
        )
    }

    @Test
    fun monthLengthHandlesLeapYears() {
        assertEquals(29, MonthPeriod(year = 2024, monthNumber = 2).numberOfDays)
        assertEquals(28, MonthPeriod(year = 2026, monthNumber = 2).numberOfDays)
        assertEquals(31, MonthPeriod(year = 2026, monthNumber = 3).numberOfDays)
        assertEquals(31, MonthPeriod(year = 2026, monthNumber = 3).days().size)
    }

    @Test
    fun previousAndNextCrossYearBoundaries() {
        assertEquals(MonthPeriod(2025, 12), MonthPeriod(2026, 1).previous())
        assertEquals(MonthPeriod(2027, 1), MonthPeriod(2026, 12).next())
        assertTrue(MonthPeriod(2026, 2) > MonthPeriod(2026, 1))
    }

    @Test
    fun weekStartsOnSaturday() {
        val weekStart = startOfWeek(LocalDate(2026, 10, 27))
        assertEquals(LocalDate(2026, 10, 24), weekStart)
        assertEquals(DayOfWeek.SATURDAY, weekStart.dayOfWeek)
    }

    @Test
    fun dayBoundsAreHalfOpenInCairo() {
        val date = LocalDate(2026, 3, 15)
        assertEquals(LocalDateTime(2026, 3, 15, 0, 0), dayStart(date).toCairoDateTime())
        assertEquals(LocalDateTime(2026, 3, 16, 0, 0), dayEndExclusive(date).toCairoDateTime())
    }

    @Test
    fun summerDatesStillResolveToCairoMidnight() {
        assertEquals(LocalDateTime(2026, 7, 1, 0, 0), LocalDate(2026, 7, 1).atCairoStart().toCairoDateTime())
        assertEquals("2026-03-15", formatIsoDate(LocalDate(2026, 3, 15)))
    }
}

package com.eec.core.time

import com.eec.core.model.MonthPeriod
import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

val CairoTimeZone: TimeZone = TimeZone.of("Africa/Cairo")

fun Instant.toCairoDate(): LocalDate = toLocalDateTime(CairoTimeZone).date

fun Instant.toCairoDateTime(): LocalDateTime = toLocalDateTime(CairoTimeZone)

fun LocalDate.atCairoStart(): Instant = atStartOfDayIn(CairoTimeZone)

fun MonthPeriod.startInstant(): Instant = firstDay.atCairoStart()

fun MonthPeriod.endExclusiveInstant(): Instant = firstDay.plus(1, DateTimeUnit.MONTH).atCairoStart()

fun MonthPeriod.contains(date: LocalDate): Boolean =
    date >= firstDay && date <= lastDay

fun MonthPeriod.days(): List<LocalDate> =
    (0 until numberOfDays).map { offset -> firstDay.plus(offset, DateTimeUnit.DAY) }

fun dayStart(date: LocalDate): Instant = date.atCairoStart()

fun dayEndExclusive(date: LocalDate): Instant = date.plus(1, DateTimeUnit.DAY).atCairoStart()

fun startOfWeek(date: LocalDate): LocalDate {
    val daysSinceSaturday = (date.dayOfWeek.ordinal - DayOfWeek.SATURDAY.ordinal + 7) % 7
    return date.minus(daysSinceSaturday, DateTimeUnit.DAY)
}

fun formatIsoDate(date: LocalDate): String = date.toString()

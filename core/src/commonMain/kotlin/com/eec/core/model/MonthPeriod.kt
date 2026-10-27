package com.eec.core.model

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus

data class MonthPeriod(
    val year: Int,
    val monthNumber: Int,
) : Comparable<MonthPeriod> {
    init {
        require(monthNumber in 1..12) { "monthNumber must be between 1 and 12" }
    }

    val firstDay: LocalDate get() = LocalDate(year = year, month = monthNumber, day = 1)
    val lastDay: LocalDate get() = firstDay.plus(1, DateTimeUnit.MONTH).plus(-1, DateTimeUnit.DAY)
    val numberOfDays: Int get() = lastDay.day

    fun previous(): MonthPeriod = if (monthNumber == 1) {
        MonthPeriod(year = year - 1, monthNumber = 12)
    } else {
        MonthPeriod(year = year, monthNumber = monthNumber - 1)
    }

    fun next(): MonthPeriod = if (monthNumber == 12) {
        MonthPeriod(year = year + 1, monthNumber = 1)
    } else {
        MonthPeriod(year = year, monthNumber = monthNumber + 1)
    }

    override fun compareTo(other: MonthPeriod): Int {
        val yearComparison = year.compareTo(other.year)
        return if (yearComparison != 0) yearComparison else monthNumber.compareTo(other.monthNumber)
    }

    companion object {
        fun from(date: LocalDate): MonthPeriod = MonthPeriod(year = date.year, monthNumber = date.month.number)
    }
}

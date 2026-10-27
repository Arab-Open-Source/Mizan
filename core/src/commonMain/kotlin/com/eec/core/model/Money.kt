package com.eec.core.model

data class Money(val minor: Long) : Comparable<Money> {
    val isZero: Boolean get() = minor == 0L
    val isNegative: Boolean get() = minor < 0L
    val isPositive: Boolean get() = minor > 0L

    operator fun plus(other: Money): Money = Money(minor + other.minor)
    operator fun minus(other: Money): Money = Money(minor - other.minor)
    operator fun unaryMinus(): Money = Money(-minor)
    operator fun times(factor: Int): Money = Money(minor * factor)

    fun absolute(): Money = if (isNegative) Money(-minor) else this

    override fun compareTo(other: Money): Int = minor.compareTo(other.minor)

    companion object {
        val Zero: Money = Money(0L)

        fun ofMinor(minor: Long): Money = Money(minor)

        fun ofEgp(egp: Long): Money = Money(egp * 100L)

        fun total(values: Iterable<Money>): Money = Money(values.sumOf { it.minor })

        fun parse(text: String): Money? {
            val trimmed = text.trim()
            if (trimmed.isEmpty()) return null
            val negative = trimmed.startsWith("-")
            val digits = if (negative) trimmed.substring(1) else trimmed
            if (digits.isEmpty()) return null
            val parts = digits.split(".")
            if (parts.size > 2) return null
            val whole = parts[0].ifEmpty { "0" }.toLongOrNull() ?: return null
            val fractionText = parts.getOrNull(1).orEmpty()
            if (fractionText.length > 2 || fractionText.any { !it.isDigit() }) return null
            val fraction = fractionText.padEnd(2, '0').toLong()
            val magnitude = whole * 100L + fraction
            return Money(if (negative) -magnitude else magnitude)
        }
    }
}

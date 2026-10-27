package com.eec.core.format

object AmountFormatter {
    fun format(minor: Long): String {
        val magnitude = magnitudeOf(minor)
        val grouped = group((magnitude / 100u).toString())
        val fraction = (magnitude % 100u).toString().padStart(2, '0')
        return signOf(minor) + grouped + "." + fraction
    }

    fun formatWithCurrency(minor: Long, currency: String = "ج.م"): String = format(minor) + " " + currency

    fun formatCsv(minor: Long): String {
        val magnitude = magnitudeOf(minor)
        val fraction = (magnitude % 100u).toString().padStart(2, '0')
        return signOf(minor) + (magnitude / 100u).toString() + "." + fraction
    }

    fun parseMinor(text: String): Long? = com.eec.core.model.Money.parse(text)?.minor

    private fun magnitudeOf(minor: Long): ULong =
        if (minor < 0L) 0uL - minor.toULong() else minor.toULong()

    private fun signOf(minor: Long): String = if (minor < 0L) "-" else ""

    private fun group(digits: String): String = buildString {
        digits.forEachIndexed { index, character ->
            if (index > 0 && (digits.length - index) % 3 == 0) append(',')
            append(character)
        }
    }
}

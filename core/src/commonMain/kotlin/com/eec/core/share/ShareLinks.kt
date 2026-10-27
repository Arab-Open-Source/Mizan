package com.eec.core.share

object ShareLinks {
    fun whatsApp(text: String): String = "https://wa.me/?text=" + encode(text)

    fun telegram(text: String): String = "https://t.me/share/url?url=&text=" + encode(text)

    fun email(subject: String, body: String): String =
        "mailto:?subject=" + encode(subject) + "&body=" + encode(body)

    fun encode(value: String): String = buildString {
        value.encodeToByteArray().forEach { byte ->
            val unsigned = byte.toInt() and 0xFF
            if (isUnreserved(unsigned)) {
                append(unsigned.toChar())
            } else {
                append('%')
                append(HEX[unsigned shr 4])
                append(HEX[unsigned and 0x0F])
            }
        }
    }

    private fun isUnreserved(value: Int): Boolean =
        value in 'A'.code..'Z'.code ||
            value in 'a'.code..'z'.code ||
            value in '0'.code..'9'.code ||
            value == '-'.code ||
            value == '.'.code ||
            value == '_'.code ||
            value == '~'.code

    private const val HEX: String = "0123456789ABCDEF"
}

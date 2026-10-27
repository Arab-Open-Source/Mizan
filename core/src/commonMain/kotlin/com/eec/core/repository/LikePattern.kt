package com.eec.core.repository

object LikePattern {
    fun contains(query: String): String = "%" + escape(query) + "%"

    fun escape(value: String): String = buildString {
        value.forEach { character ->
            when (character) {
                '\\', '%', '_' -> {
                    append('\\')
                    append(character)
                }

                else -> append(character)
            }
        }
    }
}

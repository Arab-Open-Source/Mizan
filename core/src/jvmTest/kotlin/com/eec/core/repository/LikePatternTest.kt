package com.eec.core.repository

import kotlin.test.Test
import kotlin.test.assertEquals

class LikePatternTest {
    @Test
    fun escapesLikeWildcardsAndBackslash() {
        assertEquals("50\\%\\_\\\\", LikePattern.escape("50%_\\"))
        assertEquals("%abc%", LikePattern.contains("abc"))
        assertEquals("%100\\%%", LikePattern.contains("100%"))
    }
}

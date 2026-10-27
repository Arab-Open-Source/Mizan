package com.eec.core.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PinLockTest {
    @Test
    fun matchesSha256Vectors() {
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            PinLock.sha256Hex(ByteArray(0)),
        )
        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            PinLock.sha256Hex("abc".encodeToByteArray()),
        )
        assertEquals(
            "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1",
            PinLock.sha256Hex("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray()),
        )
    }

    @Test
    fun hashesPinDeterministically() {
        val first = PinLock.hash("1234")
        val second = PinLock.hash("1234")

        assertEquals(first, second)
        assertEquals(64, first.length)
        assertTrue(first.all { it.isDigit() || it in 'a'..'f' })
        assertFalse(first == PinLock.hash("4321"))
    }

    @Test
    fun verifiesOnlyTheMatchingPin() {
        val hash = PinLock.hash("246810")

        assertTrue(PinLock.verify("246810", hash))
        assertFalse(PinLock.verify("246811", hash))
        assertFalse(PinLock.verify("1234", hash))
        assertFalse(PinLock.verify("abc", hash))
    }

    @Test
    fun rejectsInvalidPins() {
        assertTrue(PinLock.isValidPin("1234"))
        assertTrue(PinLock.isValidPin("123456"))
        assertFalse(PinLock.isValidPin("123"))
        assertFalse(PinLock.isValidPin("1234567"))
        assertFalse(PinLock.isValidPin("12a4"))
        assertFalse(PinLock.isValidPin(""))
        assertFailsWith<IllegalArgumentException> { PinLock.hash("12") }
    }
}

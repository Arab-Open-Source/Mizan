package com.eec.core.security

object PinLock {
    const val MIN_LENGTH: Int = 4
    const val MAX_LENGTH: Int = 6

    fun isValidPin(pin: String): Boolean =
        pin.length in MIN_LENGTH..MAX_LENGTH && pin.all { it.isDigit() }

    fun hash(pin: String): String {
        require(isValidPin(pin)) { "PIN must be $MIN_LENGTH to $MAX_LENGTH digits" }
        var digest = sha256(("$SALT$pin").encodeToByteArray())
        repeat(ITERATIONS - 1) {
            digest = sha256(digest)
        }
        return digest.toHex()
    }

    fun verify(pin: String, hash: String): Boolean =
        isValidPin(pin) && constantTimeEquals(this.hash(pin), hash)

    internal fun sha256Hex(input: ByteArray): String = sha256(input).toHex()

    private fun sha256(message: ByteArray): ByteArray {
        val bitLength = message.size.toLong() * BITS_PER_BYTE
        val paddingSize = (PADDING_TARGET - (message.size + 1) % BLOCK_SIZE + BLOCK_SIZE) % BLOCK_SIZE
        val padded = ByteArray(message.size + 1 + paddingSize + LENGTH_BYTES)
        message.copyInto(padded)
        padded[message.size] = 0x80.toByte()
        for (index in 0 until LENGTH_BYTES) {
            padded[padded.size - LENGTH_BYTES + index] =
                (bitLength ushr (LENGTH_BITS - BITS_PER_BYTE * (index + 1))).toByte()
        }

        val state = INITIAL_STATE.copyOf()
        val schedule = IntArray(ROUND_COUNT)
        var offset = 0
        while (offset < padded.size) {
            for (index in 0 until 16) {
                schedule[index] = readInt(padded, offset + index * 4)
            }
            for (index in 16 until ROUND_COUNT) {
                schedule[index] = schedule[index - 16] + smallSigma0(schedule[index - 15]) +
                    schedule[index - 7] + smallSigma1(schedule[index - 2])
            }

            var a = state[0]
            var b = state[1]
            var c = state[2]
            var d = state[3]
            var e = state[4]
            var f = state[5]
            var g = state[6]
            var h = state[7]

            for (index in 0 until ROUND_COUNT) {
                val temp1 = h + bigSigma1(e) + choose(e, f, g) + K[index] + schedule[index]
                val temp2 = bigSigma0(a) + majority(a, b, c)
                h = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }

            state[0] += a
            state[1] += b
            state[2] += c
            state[3] += d
            state[4] += e
            state[5] += f
            state[6] += g
            state[7] += h
            offset += BLOCK_SIZE
        }

        val result = ByteArray(DIGEST_BYTES)
        state.forEachIndexed { index, word ->
            result[index * 4] = (word ushr 24).toByte()
            result[index * 4 + 1] = (word ushr 16).toByte()
            result[index * 4 + 2] = (word ushr 8).toByte()
            result[index * 4 + 3] = word.toByte()
        }
        return result
    }

    private fun readInt(source: ByteArray, offset: Int): Int =
        ((source[offset].toInt() and 0xFF) shl 24) or
            ((source[offset + 1].toInt() and 0xFF) shl 16) or
            ((source[offset + 2].toInt() and 0xFF) shl 8) or
            (source[offset + 3].toInt() and 0xFF)

    private fun bigSigma0(value: Int): Int =
        value.rotateRight(2) xor value.rotateRight(13) xor value.rotateRight(22)

    private fun bigSigma1(value: Int): Int =
        value.rotateRight(6) xor value.rotateRight(11) xor value.rotateRight(25)

    private fun smallSigma0(value: Int): Int =
        value.rotateRight(7) xor value.rotateRight(18) xor (value ushr 3)

    private fun smallSigma1(value: Int): Int =
        value.rotateRight(17) xor value.rotateRight(19) xor (value ushr 10)

    private fun choose(x: Int, y: Int, z: Int): Int = (x and y) xor (x.inv() and z)

    private fun majority(x: Int, y: Int, z: Int): Int = (x and y) xor (x and z) xor (y and z)

    private fun Int.rotateRight(bits: Int): Int = (this ushr bits) or (this shl (Int.SIZE_BITS - bits))

    private fun ByteArray.toHex(): String = joinToString(separator = "") { byte ->
        (byte.toInt() and 0xFF).toString(HEX_RADIX).padStart(2, '0')
    }

    private fun constantTimeEquals(left: String, right: String): Boolean {
        if (left.length != right.length) return false
        var difference = 0
        for (index in left.indices) {
            difference = difference or (left[index].code xor right[index].code)
        }
        return difference == 0
    }

    private const val ITERATIONS: Int = 1000
    private const val SALT: String = "eec-pin-v1"
    private const val BLOCK_SIZE: Int = 64
    private const val PADDING_TARGET: Int = 56
    private const val DIGEST_BYTES: Int = 32
    private const val LENGTH_BYTES: Int = 8
    private const val LENGTH_BITS: Int = 64
    private const val BITS_PER_BYTE: Int = 8
    private const val ROUND_COUNT: Int = 64
    private const val HEX_RADIX: Int = 16

    private val INITIAL_STATE: IntArray = intArrayOf(
        0x6a09e667, 0xbb67ae85.toInt(), 0x3c6ef372, 0xa54ff53a.toInt(),
        0x510e527f, 0x9b05688c.toInt(), 0x1f83d9ab, 0x5be0cd19,
    )

    private val K: IntArray = intArrayOf(
        0x428a2f98, 0x71374491, 0xb5c0fbcf.toInt(), 0xe9b5dba5.toInt(),
        0x3956c25b, 0x59f111f1, 0x923f82a4.toInt(), 0xab1c5ed5.toInt(),
        0xd807aa98.toInt(), 0x12835b01, 0x243185be, 0x550c7dc3,
        0x72be5d74, 0x80deb1fe.toInt(), 0x9bdc06a7.toInt(), 0xc19bf174.toInt(),
        0xe49b69c1.toInt(), 0xefbe4786.toInt(), 0x0fc19dc6, 0x240ca1cc,
        0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
        0x983e5152.toInt(), 0xa831c66d.toInt(), 0xb00327c8.toInt(), 0xbf597fc7.toInt(),
        0xc6e00bf3.toInt(), 0xd5a79147.toInt(), 0x06ca6351, 0x14292967,
        0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
        0x650a7354, 0x766a0abb, 0x81c2c92e.toInt(), 0x92722c85.toInt(),
        0xa2bfe8a1.toInt(), 0xa81a664b.toInt(), 0xc24b8b70.toInt(), 0xc76c51a3.toInt(),
        0xd192e819.toInt(), 0xd6990624.toInt(), 0xf40e3585.toInt(), 0x106aa070,
        0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
        0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
        0x748f82ee, 0x78a5636f, 0x84c87814.toInt(), 0x8cc70208.toInt(),
        0x90befffa.toInt(), 0xa4506ceb.toInt(), 0xbef9a3f7.toInt(), 0xc67178f2.toInt(),
    )
}

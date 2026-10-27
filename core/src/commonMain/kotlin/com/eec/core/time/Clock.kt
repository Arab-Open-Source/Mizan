package com.eec.core.time

import kotlin.time.Instant

fun interface Clock {
    fun now(): Instant
}

object SystemClock : Clock {
    override fun now(): Instant = kotlin.time.Clock.System.now()
}

class FixedClock(private var instant: Instant) : Clock {
    override fun now(): Instant = instant

    fun set(instant: Instant) {
        this.instant = instant
    }

    fun advance(millis: Long) {
        instant = Instant.fromEpochMilliseconds(instant.toEpochMilliseconds() + millis)
    }
}

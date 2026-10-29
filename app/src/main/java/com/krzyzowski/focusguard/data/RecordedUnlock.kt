package com.krzyzowski.focusguard.data

import java.time.Duration
import java.time.Instant

data class RecordedUnlock(val at: Instant, val previousUnlock: Instant?, val unlocksToday: Int) {

    // A null window means no limit.
    fun needsPrompt(window: Duration?): Boolean = when {
        window == null -> true
        previousUnlock == null -> false
        else -> Duration.between(previousUnlock, at) <= window
    }
}

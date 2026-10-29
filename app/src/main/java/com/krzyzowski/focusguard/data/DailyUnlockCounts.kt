package com.krzyzowski.focusguard.data

import java.time.LocalDate

class DailyUnlockCounts private constructor(private val counts: Map<LocalDate, Int>) {

    fun countOn(date: LocalDate): Int = counts[date] ?: 0

    fun recordUnlockOn(date: LocalDate): DailyUnlockCounts {
        val oldestKept = date.minusDays(RETENTION_DAYS - 1)
        val updated = counts + (date to countOn(date) + 1)
        return DailyUnlockCounts(updated.filterKeys { !it.isBefore(oldestKept) })
    }

    fun lastDays(today: LocalDate, days: Int): List<Pair<LocalDate, Int>> =
        (days - 1 downTo 0).map { daysAgo -> today.minusDays(daysAgo.toLong()).let { it to countOn(it) } }

    fun encode(): String = counts.entries
        .sortedBy { it.key }
        .joinToString(",") { "${it.key}=${it.value}" }

    companion object {
        const val RETENTION_DAYS = 28L

        val NONE = DailyUnlockCounts(emptyMap())

        fun decode(encoded: String): DailyUnlockCounts = DailyUnlockCounts(
            encoded.split(',')
                .filter { it.isNotBlank() }
                .associate { entry ->
                    val (date, count) = entry.split('=')
                    LocalDate.parse(date) to count.toInt()
                },
        )
    }
}

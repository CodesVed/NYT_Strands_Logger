package com.example.strandslogger.data.stats

import java.time.DayOfWeek
import java.time.LocalDate

object StreakCalculator {

    fun currentStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val sorted = dates.distinct().sortedDescending()
        val today = LocalDate.now()

        if (sorted.first() != today && sorted.first() != today.minusDays(1)) return 0

        var streak = 0
        var expected = sorted.first()
        for (date in sorted) {
            if (date == expected) {
                streak++
                expected = expected.minusDays(1)
            } else break
        }

        return streak
    }

    fun longestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val sorted = dates.distinct().sorted()
        var longest = 1
        var current = 1

        for (i in 1 until sorted.size) {
            current = if (sorted[i] == sorted [i-1].plusDays(1)) current + 1 else 1
            longest = maxOf(longest, current)
        }

        return longest
    }

    fun mostActiveWeekday(dates: List<LocalDate>): DayOfWeek? =
        dates.groupingBy { it.dayOfWeek }.eachCount().maxByOrNull { it.value }?.key
}
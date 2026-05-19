package com.shiji.app.domain

import com.shiji.app.data.entity.Record
import com.shiji.app.domain.model.Achievement
import com.shiji.app.domain.model.Achievements
import com.shiji.app.util.isConsecutiveDay
import com.shiji.app.util.toDayKey
import java.util.Calendar
import java.util.concurrent.TimeUnit

object AchievementCalculator {

    fun calculate(records: List<Record>): List<Achievement> {
        if (records.isEmpty()) return Achievements.ALL.map { it.copy(isUnlocked = false) }

        val sorted = records.sortedBy { it.timestamp }
        val totalRecords = sorted.size

        // Compute derived stats
        val uniqueDays = sorted.map { toDayKey(it.timestamp) }.distinct().sorted()
        val currentStreak = computeCurrentStreak(uniqueDays)
        val longestStreak = computeLongestStreak(uniqueDays)
        val bristolSet = sorted.map { it.bristolType }.toSet()
        val colorSet = sorted.map { it.color }.toSet()
        val dayCounts = sorted.groupBy { toDayKey(it.timestamp) }.mapValues { it.value.size }
        val maxRecordsInDay = dayCounts.values.maxOrNull() ?: 0

        // Hour counts
        val hourCounts = sorted.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            cal.get(Calendar.HOUR_OF_DAY)
        }.mapValues { it.value.size }
        val hasGoldenHour = hourCounts.any { it.value >= 10 }

        // Check for long interval
        var hasLongInterval = false
        for (i in 1 until uniqueDays.size) {
            val parts1 = uniqueDays[i - 1].split("-").map { it.toInt() }
            val parts2 = uniqueDays[i].split("-").map { it.toInt() }
            val cal1 = Calendar.getInstance().apply { set(parts1[0], 0, 1); add(Calendar.DAY_OF_YEAR, parts1[1] - 1) }
            val cal2 = Calendar.getInstance().apply { set(parts2[0], 0, 1); add(Calendar.DAY_OF_YEAR, parts2[1] - 1) }
            val diffDays = TimeUnit.MILLISECONDS.toDays(cal2.timeInMillis - cal1.timeInMillis)
            if (diffDays > 7) {
                hasLongInterval = true
                break
            }
        }

        // Check early bird
        val hasEarlyBird = sorted.any {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            cal.get(Calendar.HOUR_OF_DAY) in 4..6
        }

        val firstRecordTime = sorted.first().timestamp
        val lastRecordTime = sorted.last().timestamp

        return Achievements.ALL.map { achievement ->
            val unlocked = when (achievement.id) {
                "first_record" -> totalRecords >= 1
                "streak_3" -> longestStreak >= 3
                "streak_7" -> longestStreak >= 7
                "streak_14" -> longestStreak >= 14
                "streak_30" -> longestStreak >= 30
                "total_10" -> totalRecords >= 10
                "total_50" -> totalRecords >= 50
                "total_100" -> totalRecords >= 100
                "bristol_4" -> bristolSet.contains(4)
                "early_bird" -> hasEarlyBird
                "all_bristol" -> bristolSet.size >= 7
                "all_colors" -> colorSet.size >= 7
                "triple_day" -> maxRecordsInDay >= 3
                "golden_hour" -> hasGoldenHour
                "long_interval" -> totalRecords >= 2 && hasLongInterval
                else -> false
            }

            val unlockedTime = if (unlocked) lastRecordTime else 0

            achievement.copy(isUnlocked = unlocked, unlockedAt = unlockedTime)
        }
    }

    private fun computeCurrentStreak(sortedUniqueDays: List<String>): Int {
        if (sortedUniqueDays.isEmpty()) return 0
        val reversed = sortedUniqueDays.reversed()
        var streak = 1
        for (i in 1 until reversed.size) {
            if (isConsecutiveDay(reversed[i], reversed[i - 1])) streak++ else break
        }
        return streak
    }

    private fun computeLongestStreak(sortedUniqueDays: List<String>): Int {
        if (sortedUniqueDays.isEmpty()) return 0
        var longest = 1
        var current = 1
        for (i in 1 until sortedUniqueDays.size) {
            if (isConsecutiveDay(sortedUniqueDays[i - 1], sortedUniqueDays[i])) {
                current++
                longest = maxOf(longest, current)
            } else {
                current = 1
            }
        }
        return longest
    }
}

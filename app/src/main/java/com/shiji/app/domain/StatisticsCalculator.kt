package com.shiji.app.domain

import com.shiji.app.data.entity.Record
import com.shiji.app.domain.model.StatisticsData
import com.shiji.app.util.isConsecutiveDay
import com.shiji.app.util.toDayKey
import java.util.Calendar
import java.util.concurrent.TimeUnit

object StatisticsCalculator {

    fun calculate(records: List<Record>): StatisticsData {
        if (records.isEmpty()) return StatisticsData()

        val sorted = records.sortedBy { it.timestamp }
        val totalRecords = sorted.size

        // Longest interval between records
        var longestInterval = 0
        for (i in 1 until sorted.size) {
            val diff = sorted[i].timestamp - sorted[i - 1].timestamp
            val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
            if (days > longestInterval) longestInterval = days
        }

        // Golden hour - most common 2-hour window
        val hourCounts = mutableMapOf<Int, Int>()
        sorted.forEach { record ->
            val cal = Calendar.getInstance().apply { timeInMillis = record.timestamp }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            hourCounts[hour] = (hourCounts[hour] ?: 0) + 1
        }
        val goldenHour = if (hourCounts.isNotEmpty()) {
            val peak = hourCounts.maxBy { it.value }
            val start = peak.key
            val end = (start + 2) % 24
            "${String.format("%02d", start)}:00-${String.format("%02d", end)}:00"
        } else "未记录"

        // Average Bristol
        val averageBristol = sorted.map { it.bristolType }.average().toFloat()

        // Most common Bristol
        val bristolCounts = sorted.groupBy { it.bristolType }.mapValues { it.value.size }
        val mostCommonBristol = bristolCounts.maxByOrNull { it.value }?.key?.let { b ->
            com.shiji.app.domain.model.BristolType.fromValue(b).label
        } ?: "未记录"

        // Most common color
        val colorCounts = sorted.groupBy { it.color }.mapValues { it.value.size }
        val mostCommonColor = colorCounts.maxByOrNull { it.value }?.key ?: "未记录"

        // Streaks
        val uniqueDays = sorted.map { toDayKey(it.timestamp) }.distinct().sorted()
        var longestStreak = 0
        var currentStreakCount = 1
        for (i in 1 until uniqueDays.size) {
            if (isConsecutiveDay(uniqueDays[i - 1], uniqueDays[i])) {
                currentStreakCount++
            } else {
                longestStreak = maxOf(longestStreak, currentStreakCount)
                currentStreakCount = 1
            }
        }
        longestStreak = maxOf(longestStreak, currentStreakCount)

        // Current streak (from latest day backward)
        val sortedDays = uniqueDays.reversed()
        var currentStreak = 1
        if (sortedDays.size > 1) {
            for (i in 1 until sortedDays.size) {
                if (isConsecutiveDay(sortedDays[i], sortedDays[i - 1])) {
                    currentStreak++
                } else break
            }
        }

        return StatisticsData(
            totalRecords = totalRecords,
            longestIntervalDays = longestInterval,
            goldenHour = goldenHour,
            averageBristol = averageBristol,
            mostCommonBristol = mostCommonBristol,
            mostCommonColor = mostCommonColor,
            longestStreak = longestStreak,
            currentStreak = currentStreak,
            bristolDistribution = bristolCounts,
            colorDistribution = colorCounts,
            totalDaysRecorded = uniqueDays.size,
            startDate = sorted.first().timestamp,
            endDate = sorted.last().timestamp
        )
    }


}

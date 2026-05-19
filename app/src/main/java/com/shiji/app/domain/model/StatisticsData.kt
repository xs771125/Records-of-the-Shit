package com.shiji.app.domain.model

data class StatisticsData(
    val totalRecords: Int = 0,
    val longestIntervalDays: Int = 0,
    val goldenHour: String = "未记录",
    val averageBristol: Float = 0f,
    val mostCommonBristol: String = "未记录",
    val mostCommonColor: String = "未记录",
    val longestStreak: Int = 0,
    val currentStreak: Int = 0,
    val bristolDistribution: Map<Int, Int> = emptyMap(),
    val colorDistribution: Map<String, Int> = emptyMap(),
    val totalDaysRecorded: Int = 0,
    val startDate: Long = 0,
    val endDate: Long = 0
)

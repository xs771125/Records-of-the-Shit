package com.shiji.app.util

import java.util.Calendar

fun toDayKey(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
}

fun toDateKey(timestamp: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
    return "${cal.get(Calendar.YEAR)}-" +
        String.format("%02d", cal.get(Calendar.MONTH) + 1) +
        "-${String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))}"
}

fun isConsecutiveDay(day1: String, day2: String): Boolean {
    val parts1 = day1.split("-").map { it.toInt() }
    val parts2 = day2.split("-").map { it.toInt() }
    if (parts1[0] == parts2[0]) return parts2[1] - parts1[1] == 1
    return parts2[0] - parts1[0] == 1 && parts1[1] > 350 && parts2[1] == 1
}

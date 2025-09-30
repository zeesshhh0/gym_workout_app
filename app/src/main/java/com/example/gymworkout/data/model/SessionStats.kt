package com.example.gymworkout.data.model

import java.util.concurrent.TimeUnit

data class SessionStats(
    val durationMinutes: Long,
    val exerciseCount: Int,
    val totalVolume: Float
) {
    fun getDurationText(): String {
        val hours = TimeUnit.MINUTES.toHours(durationMinutes)
        val minutes = durationMinutes % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
}
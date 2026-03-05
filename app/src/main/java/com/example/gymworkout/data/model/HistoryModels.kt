package com.example.gymworkout.data.model

sealed class HistoryItem {
    data class MonthHeader(val monthName: String, val workoutCount: Int) : HistoryItem()
    data class WorkoutCard(
        val sessionId: Int,
        val workoutName: String,
        val date: String,
        val startTime: String?,
        val endTime: String?,
        val stats: SessionStats,
        val exercises: List<HistoryExercise>
    ) : HistoryItem()
}

data class HistoryExercise(
    val exerciseName: String,
    val setCount: Int,
    val bestSetWeight: Float,
    val bestSetReps: Int
)

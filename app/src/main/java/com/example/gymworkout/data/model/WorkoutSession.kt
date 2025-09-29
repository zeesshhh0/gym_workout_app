package com.example.gymworkout.data.model

data class WorkoutSession(
    val id: Int,
    val workoutId: Int,
    val workoutName: String, // Added for display in sessions list
    val date: String,
    val startTime: String?,
    val endTime: String?,
    val notes: String?
)
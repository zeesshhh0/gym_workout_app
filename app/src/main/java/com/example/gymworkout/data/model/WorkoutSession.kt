package com.example.gymworkout.data.model

data class WorkoutSession(
    val id: Int,
    val workoutId: Int,
    val exerciseId: Int,
    val sets: Int,
    val reps: Int,
    val weight: Int
)

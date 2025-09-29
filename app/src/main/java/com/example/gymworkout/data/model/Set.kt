package com.example.gymworkout.data.model

data class Set(
    val id: Int,
    val sessionId: Int,
    val exerciseId: Int,
    val setNumber: Int,
    val weightUsed: Float,
    val reps: Int
)

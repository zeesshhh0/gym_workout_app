package com.example.gymworkout.data.model

data class Exercise(val id: Int, val name: String, val muscleGroup: String, var workoutId: Int = 0)
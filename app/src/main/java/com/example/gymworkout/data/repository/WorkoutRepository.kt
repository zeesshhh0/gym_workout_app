package com.example.gymworkout.data.repository

import android.content.Context
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.model.MuscleGroup
import com.example.gymworkout.data.model.SessionStats
import com.example.gymworkout.data.model.Workout
import com.example.gymworkout.data.model.WorkoutSession
import com.google.firebase.auth.FirebaseAuth
import com.example.gymworkout.data.model.Set as ExcerciseSet

class WorkoutRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // User methods
    fun getUserName(): String? = dbHelper.getUserName(currentUserId)
    fun getUserDetails(): Pair<String, String>? = dbHelper.getUserDetails(currentUserId)
    fun updateUserDetails(username: String, email: String) = 
        dbHelper.updateUserDetails(currentUserId, username, email)
    fun addUser(username: String, email: String) = 
        dbHelper.addUser(currentUserId, username, email, "")

    // Workout methods
    fun addWorkout(name: String, description: String): Long = 
        dbHelper.addWorkout(currentUserId, name, description)
    fun getLatestWorkoutForUser(): Workout? = 
        dbHelper.getLatestWorkoutForUser(currentUserId)
    fun updateWorkoutName(workoutId: Long, newName: String) = 
        dbHelper.updateWorkoutName(currentUserId, workoutId, newName)
    fun getWorkoutName(workoutId: Long): String = 
        dbHelper.getWorkoutName(currentUserId, workoutId)

    // Session methods
    fun getOrCreateWorkoutSession(workoutId: Int): Int = 
        dbHelper.getOrCreateWorkoutSession(currentUserId, workoutId)
    fun getAllWorkoutSessions(): List<WorkoutSession> = 
        dbHelper.getAllWorkoutSessions(currentUserId)
    fun getLatestWorkoutSession(): WorkoutSession? = 
        dbHelper.getLatestWorkoutSession(currentUserId)
    fun getSessionStats(sessionId: Int): SessionStats = 
        dbHelper.getSessionStats(currentUserId, sessionId)
    fun updateWorkoutSessionStartTime(sessionId: Int, startTime: String) = 
        dbHelper.updateWorkoutSessionStartTime(currentUserId, sessionId, startTime)
    fun updateWorkoutSessionEndTime(sessionId: Int, endTime: String) = 
        dbHelper.updateWorkoutSessionEndTime(currentUserId, sessionId, endTime)
    fun deleteWorkoutAndSession(workoutId: Long, sessionId: Int) = 
        dbHelper.deleteWorkoutAndSession(currentUserId, workoutId, sessionId)
    fun deleteWorkoutSession(sessionId: Int) = 
        dbHelper.deleteWorkoutSession(currentUserId, sessionId)

    // Exercise methods
    fun getAllExercises(): List<Exercise> = dbHelper.getAllExercises()
    fun getExerciseInstructions(exerciseName: String): String? = dbHelper.getExerciseInstructions(exerciseName)
    fun getAllMuscleGroups(): List<MuscleGroup> = dbHelper.getAllMuscleGroups()
    fun addExercise(muscleGroupId: Int, name: String, description: String, instructions: String): Long = 
        dbHelper.addExercise(muscleGroupId, name, description, instructions)
    fun addExerciseToWorkout(workoutId: Int, exerciseId: Int): Long = 
        dbHelper.addExerciseToWorkout(currentUserId, workoutId, exerciseId)
    fun getExercisesForWorkout(workoutId: Int): List<Exercise> = 
        dbHelper.getExercisesForWorkout(currentUserId, workoutId)
    fun getExercisesForSession(sessionId: Int): List<Exercise> = 
        dbHelper.getExercisesForSession(currentUserId, sessionId)

    // Set methods
    fun addSet(workoutId: Int, exerciseId: Int, sets: Int, reps: Int, weight: Float) = 
        dbHelper.addSet(currentUserId, workoutId, exerciseId, sets, reps, weight)
    fun getSetsForExercise(sessionId: Int, exerciseId: Int): List<ExcerciseSet> =
        dbHelper.getSetsForExercise(currentUserId, sessionId, exerciseId)
    fun getSetsForExerciseInSession(sessionId: Int, exerciseId: Int): List<ExcerciseSet> =
        dbHelper.getSetsForExerciseInSession(currentUserId, sessionId, exerciseId)
    fun updateSet(setId: Int, reps: Int, weight: Float) = 
        dbHelper.updateSet(currentUserId, setId, reps, weight)
    fun deleteSet(setId: Int) = 
        dbHelper.deleteSet(currentUserId, setId)

    // Stats
    fun calculateWorkoutStreak(): Int = dbHelper.calculateWorkoutStreak(currentUserId)
}

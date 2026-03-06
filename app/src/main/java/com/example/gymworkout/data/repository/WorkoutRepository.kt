package com.example.gymworkout.data.repository

import android.content.Context
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.model.MuscleGroup
import com.example.gymworkout.data.model.SessionStats
import com.example.gymworkout.data.model.Workout
import com.example.gymworkout.data.model.WorkoutSession
import com.example.gymworkout.data.sync.FirestoreSyncManager
import com.google.firebase.auth.FirebaseAuth
import com.example.gymworkout.data.model.Set as ExcerciseSet

class WorkoutRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val auth = FirebaseAuth.getInstance()
    private val firestoreSyncManager = FirestoreSyncManager()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "local_user"

    fun setSyncListeners(onFailure: (String) -> Unit, onUnauthenticated: () -> Unit) {
        firestoreSyncManager.onSyncFailure = onFailure
        firestoreSyncManager.onUnauthenticated = onUnauthenticated
    }

    // User methods
    fun getUserName(): String? = dbHelper.getUserName(currentUserId)
    fun getUserDetails(): Pair<String, String>? = dbHelper.getUserDetails(currentUserId)
    fun addUser(username: String, email: String) {
        dbHelper.addUser(currentUserId, username, email, "")
        firestoreSyncManager.syncUserProfile(username, email)
    }

    // Workout methods
    fun addWorkout(name: String): Long {
        val id = dbHelper.addWorkout(currentUserId, name)
        if (id != -1L) {
            val workout = Workout(id.toInt(), name)
            firestoreSyncManager.syncWorkout(workout)
        }
        return id
    }
    fun getLatestWorkoutForUser(): Workout? = 
        dbHelper.getLatestWorkoutForUser(currentUserId)
    fun updateWorkoutName(workoutId: Long, newName: String) {
        dbHelper.updateWorkoutName(currentUserId, workoutId, newName)
        val workout = Workout(workoutId.toInt(), newName)
        firestoreSyncManager.syncWorkout(workout)
    }
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
    fun updateWorkoutSessionEndTime(sessionId: Int, endTime: String) {
        dbHelper.updateWorkoutSessionEndTime(currentUserId, sessionId, endTime)
        // Sync session and its exercises
        val sessions = dbHelper.getAllWorkoutSessions(currentUserId)
        val session = sessions.find { it.id == sessionId }
        if (session != null) {
            firestoreSyncManager.syncSession(session, session.workoutId.toString())
            val exercises = dbHelper.getExercisesForSession(currentUserId, sessionId)
            exercises.forEach { exercise ->
                // Since the Firestore structure now places exercises directly under the workout,
                // we should ideally sync all sets for this exercise across the whole workout.
                // However, our local query is currently session-scoped. We must get sets for the whole workout
                // to avoid overwriting previous sessions' sets in Firestore.
                val sets = dbHelper.getSetsForExerciseInWorkout(currentUserId, session.workoutId, exercise.id)
                firestoreSyncManager.syncExerciseSets(exercise, session.workoutId.toString(), sets)
            }
        }
    }
    fun deleteWorkoutAndSession(workoutId: Long, sessionId: Int) {
        dbHelper.deleteWorkoutAndSession(currentUserId, workoutId, sessionId)
        firestoreSyncManager.deleteSession(workoutId.toString(), sessionId.toString())
    }
    fun deleteWorkoutSession(sessionId: Int) {
        val session = dbHelper.getAllWorkoutSessions(currentUserId).find { it.id == sessionId }
        dbHelper.deleteWorkoutSession(currentUserId, sessionId)
        if (session != null) {
            firestoreSyncManager.deleteSession(session.workoutId.toString(), sessionId.toString())
        }
    }

    // Exercise methods
    fun getAllExercises(): List<Exercise> = dbHelper.getAllExercises()
    fun getExerciseInstructions(exerciseName: String): String? = dbHelper.getExerciseInstructions(exerciseName)
    fun getAllMuscleGroups(): List<MuscleGroup> = dbHelper.getAllMuscleGroups()
    fun addExercise(muscleGroupId: Int, name: String, instructions: String): Long =
        dbHelper.addExercise(muscleGroupId, name, instructions)
    fun addExerciseToWorkout(workoutId: Int, exerciseId: Int): Long = 
        dbHelper.addExerciseToWorkout(currentUserId, workoutId, exerciseId)
    fun getExercisesForWorkout(workoutId: Int): List<Exercise> =
        dbHelper.getExercisesForWorkout(currentUserId, workoutId)
    fun deleteExerciseFromWorkout(workoutId: Int, sessionId: Int, exerciseId: Int) {
        dbHelper.deleteExerciseFromWorkout(currentUserId, workoutId, exerciseId)
        firestoreSyncManager.deleteExercise(workoutId.toString(), sessionId.toString(), exerciseId.toString())
    }
    fun getExercisesForSession(sessionId: Int): List<Exercise> =        dbHelper.getExercisesForSession(currentUserId, sessionId)

    // Set methods
    fun addSet(workoutId: Int, exerciseId: Int, sets: Int, reps: Int, weight: Float) = 
        dbHelper.addSet(currentUserId, workoutId, exerciseId, sets, reps, weight)
    fun getSetsForExercise(sessionId: Int, exerciseId: Int): List<ExcerciseSet> =
        dbHelper.getSetsForExercise(currentUserId, sessionId, exerciseId)
    fun getSetsForExerciseInSession(sessionId: Int, exerciseId: Int): List<ExcerciseSet> =
        dbHelper.getSetsForExerciseInSession(currentUserId, sessionId, exerciseId)
    fun updateSet(setId: Int, reps: Int, weight: Float) =
        dbHelper.updateSet(currentUserId, setId, reps, weight)
    fun updateSetCompletionStatus(setId: Int, isCompleted: Boolean) =
        dbHelper.updateSetCompletionStatus(currentUserId, setId, isCompleted)
    fun deleteSet(setId: Int) =
        dbHelper.deleteSet(currentUserId, setId)
    fun clearAllData() = dbHelper.clearAllData(currentUserId)
    fun cleanUpOrphanedData() = dbHelper.cleanUpOrphanedData(currentUserId)

    fun getHistoryData(): List<com.example.gymworkout.data.model.HistoryItem> {
        val sessions = dbHelper.getAllWorkoutSessions(currentUserId)
        val historyItems = mutableListOf<com.example.gymworkout.data.model.HistoryItem>()
        
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd")
        val monthFormat = java.text.SimpleDateFormat("MMMM yyyy")
        
        val groupedByMonth = sessions.groupBy { session ->
            val date = dateFormat.parse(session.date)
            monthFormat.format(date ?: java.util.Date())
        }

        groupedByMonth.forEach { (month, monthSessions) ->
            historyItems.add(com.example.gymworkout.data.model.HistoryItem.MonthHeader(month, monthSessions.size))
            monthSessions.forEach { session ->
                val stats = dbHelper.getSessionStats(currentUserId, session.id)
                val exercises = dbHelper.getHistoryExercisesForSession(currentUserId, session.id)
                historyItems.add(
                    com.example.gymworkout.data.model.HistoryItem.WorkoutCard(
                        session.id,
                        session.workoutName,
                        session.date,
                        session.startTime,
                        session.endTime,
                        stats,
                        exercises
                    )
                )
            }
        }
        
        return historyItems
    }

    fun hasLocalData(): Boolean {
        return dbHelper.getAllWorkoutSessions(currentUserId).isNotEmpty() || 
               dbHelper.getLatestWorkoutForUser(currentUserId) != null
    }

    fun restoreUserData(data: List<FirestoreSyncManager.WorkoutData>) {
        dbHelper.clearAllData(currentUserId)
        dbHelper.restoreData(currentUserId, data)
    }

    // Stats
    fun calculateWorkoutStreak(): Int = dbHelper.calculateWorkoutStreak(currentUserId)
}

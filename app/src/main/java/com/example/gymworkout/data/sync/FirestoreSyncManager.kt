package com.example.gymworkout.data.sync

import android.util.Log
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.model.Set
import com.example.gymworkout.data.model.Workout
import com.example.gymworkout.data.model.WorkoutSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreSyncManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val uid: String? get() = auth.currentUser?.uid

    // Callbacks to notify UI
    var onSyncFailure: ((String) -> Unit)? = null
    var onUnauthenticated: (() -> Unit)? = null

    private fun getUidOrAbort(): String? {
        val currentUid = uid
        if (currentUid == null) {
            // Silently abort if user is not authenticated, allowing local-only use.
            return null
        }
        return currentUid
    }

    fun syncUserProfile(username: String, email: String) {
        val uid = getUidOrAbort() ?: return
        val userData = hashMapOf(
            "username" to username,
            "email" to email
        )
        firestore.collection("users").document(uid).set(userData, SetOptions.merge())
            .addOnFailureListener { e ->
                Log.e("FirestoreSyncManager", "Sync failed for user profile", e)
                onSyncFailure?.invoke("Failed to backup user profile.")
            }
    }

    private val userDoc: DocumentReference?
        get() = getUidOrAbort()?.let { firestore.collection("users").document(it) }

    fun syncWorkout(workout: Workout) {
        val doc = userDoc?.collection("workouts")?.document(workout.id.toString())
        doc?.set(workout.toFirestoreMap(), SetOptions.merge())?.addOnFailureListener { e ->
            Log.e("FirestoreSyncManager", "Sync failed for workout: ${doc.path}", e)
            onSyncFailure?.invoke("Sync failed. Your data is saved locally.")
        }
    }

    fun syncSession(session: WorkoutSession, workoutId: String) {
        val doc = userDoc?.collection("workouts")?.document(workoutId)
            ?.collection("sessions")?.document(session.id.toString())
        doc?.set(session.toFirestoreMap(), SetOptions.merge())?.addOnFailureListener { e ->
            Log.e("FirestoreSyncManager", "Sync failed for session: ${doc?.path}", e)
            onSyncFailure?.invoke("Sync failed. Your data is saved locally.")
        }
    }

    fun syncExerciseSets(exercise: Exercise, sessionId: String, workoutId: String, sets: List<Set>) {
        val doc = userDoc?.collection("workouts")?.document(workoutId)
            ?.collection("sessions")?.document(sessionId)
            ?.collection("exercises")?.document(exercise.id.toString())
        doc?.set(exercise.toFirestoreMap(sets), SetOptions.merge())?.addOnFailureListener { e ->
            Log.e("FirestoreSyncManager", "Sync failed for exercise sets: ${doc.path}", e)
            onSyncFailure?.invoke("Sync failed. Your data is saved locally.")
        }
    }

    fun fetchUserProfile(onSuccess: (String, String) -> Unit) {
        val uid = getUidOrAbort() ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: ""
                    val email = doc.getString("email") ?: ""
                    onSuccess(username, email)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreSyncManager", "Failed to fetch user profile", e)
            }
    }

    fun fetchAllUserData(onSuccess: (List<WorkoutData>) -> Unit, onFailure: (Exception) -> Unit) {
        val uid = getUidOrAbort() ?: run {
            onFailure(Exception("User not authenticated"))
            return
        }

        firestore.collection("users").document(uid).collection("workouts").get()
            .addOnSuccessListener { workoutDocs ->
                val allData = mutableListOf<WorkoutData>()
                if (workoutDocs.isEmpty) {
                    onSuccess(allData)
                    return@addOnSuccessListener
                }

                var pendingWorkouts = workoutDocs.size()
                for (workoutDoc in workoutDocs) {
                    val workoutId = workoutDoc.id
                    val workout = Workout(
                        id = workoutDoc.getLong("id")?.toInt() ?: workoutId.toInt(),
                        name = workoutDoc.getString("name") ?: ""
                    )

                    val sessions = mutableListOf<SessionData>()
                    workoutDoc.reference.collection("sessions").get()
                        .addOnSuccessListener { sessionDocs ->
                            var pendingSessions = sessionDocs.size()
                            if (sessionDocs.isEmpty) {
                                allData.add(WorkoutData(workout, sessions))
                                pendingWorkouts--
                                if (pendingWorkouts == 0) onSuccess(allData)
                                return@addOnSuccessListener
                            }

                            for (sessionDoc in sessionDocs) {
                                val session = WorkoutSession(
                                    id = sessionDoc.getLong("id")?.toInt() ?: sessionDoc.id.toInt(),
                                    workoutId = sessionDoc.getLong("workoutId")?.toInt() ?: workoutId.toInt(),
                                    workoutName = sessionDoc.getString("workoutName") ?: "",
                                    date = sessionDoc.getString("date") ?: "",
                                    startTime = sessionDoc.getString("startTime"),
                                    endTime = sessionDoc.getString("endTime")
                                )

                                val exercises = mutableListOf<ExerciseData>()
                                sessionDoc.reference.collection("exercises").get()
                                    .addOnSuccessListener { exerciseDocs ->
                                        for (exerciseDoc in exerciseDocs) {
                                            val exercise = Exercise(
                                                id = exerciseDoc.getLong("id")?.toInt() ?: exerciseDoc.id.toInt(),
                                                name = exerciseDoc.getString("name") ?: "",
                                                muscleGroup = exerciseDoc.getString("muscleGroup") ?: ""
                                            )
                                            val setsList = exerciseDoc.get("sets") as? List<Map<String, Any>> ?: emptyList()
                                            val sets = setsList.map {
                                                Set(
                                                    id = (it["id"] as? Long)?.toInt() ?: 0,
                                                    sessionId = (it["sessionId"] as? Long)?.toInt() ?: session.id,
                                                    exerciseId = (it["exerciseId"] as? Long)?.toInt() ?: exercise.id,
                                                    setNumber = (it["setNumber"] as? Long)?.toInt() ?: 0,
                                                    weightUsed = (it["weightUsed"] as? Double)?.toFloat() ?: 0f,
                                                    reps = (it["reps"] as? Long)?.toInt() ?: 0,
                                                    isCompleted = (it["isCompleted"] as? Boolean) ?: false
                                                )
                                            }
                                            exercises.add(ExerciseData(exercise, sets))
                                        }
                                        sessions.add(SessionData(session, exercises))
                                        pendingSessions--
                                        if (pendingSessions == 0) {
                                            allData.add(WorkoutData(workout, sessions))
                                            pendingWorkouts--
                                            if (pendingWorkouts == 0) onSuccess(allData)
                                        }
                                    }
                            }
                        }
                }
            }
            .addOnFailureListener {
                onFailure(it)
            }
    }

    data class WorkoutData(val workout: Workout, val sessions: List<SessionData>)
    data class SessionData(val session: WorkoutSession, val exercises: List<ExerciseData>)
    data class ExerciseData(val exercise: Exercise, val sets: List<Set>)

    fun deleteSession(workoutId: String, sessionId: String) {
        val sessionDoc = userDoc?.collection("workouts")?.document(workoutId)
            ?.collection("sessions")?.document(sessionId) ?: return
        
        sessionDoc.collection("exercises").get().addOnSuccessListener { exercises ->
            val batch = firestore.batch()
            for (exercise in exercises) {
                batch.delete(exercise.reference)
            }
            batch.delete(sessionDoc)
            batch.commit().addOnFailureListener { e ->
                Log.e("FirestoreSyncManager", "Delete failed for session: ${sessionDoc.path}", e)
                onSyncFailure?.invoke("Delete sync failed. Your data is saved locally.")
            }
        }.addOnFailureListener { e ->
            Log.e("FirestoreSyncManager", "Failed to fetch exercises for deletion: ${sessionDoc.path}", e)
            onSyncFailure?.invoke("Delete sync failed. Your data is saved locally.")
        }
    }

    fun deleteExercise(workoutId: String, sessionId: String, exerciseId: String) {
        val doc = userDoc?.collection("workouts")?.document(workoutId)
            ?.collection("sessions")?.document(sessionId)
            ?.collection("exercises")?.document(exerciseId) ?: return

        doc.delete().addOnFailureListener { e ->
            Log.e("FirestoreSyncManager", "Delete failed for exercise: ${doc.path}", e)
            onSyncFailure?.invoke("Delete sync failed. Your data is saved locally.")
        }
    }
}

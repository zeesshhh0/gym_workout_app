package com.example.gymworkout.data.sync

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

    private val userDoc: DocumentReference?
        get() = uid?.let { firestore.collection("users").document(it) }

    fun syncWorkout(workout: Workout) {
        val doc = userDoc?.collection("workouts")?.document(workout.id.toString())
        doc?.set(workout.toFirestoreMap(), SetOptions.merge())
    }

    fun syncSession(session: WorkoutSession, workoutId: String) {
        val doc = userDoc?.collection("workouts")?.document(workoutId)
            ?.collection("sessions")?.document(session.id.toString())
        doc?.set(session.toFirestoreMap(), SetOptions.merge())
    }

    fun syncExerciseSets(exercise: Exercise, sessionId: String, workoutId: String, sets: List<Set>) {
        val doc = userDoc?.collection("workouts")?.document(workoutId)
            ?.collection("sessions")?.document(sessionId)
            ?.collection("exercises")?.document(exercise.id.toString())
        doc?.set(exercise.toFirestoreMap(sets), SetOptions.merge())
    }

    fun fetchAllUserData(onSuccess: (List<WorkoutData>) -> Unit, onFailure: (Exception) -> Unit) {
        val uid = this.uid ?: run {
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
                        name = workoutDoc.getString("name") ?: "",
                        description = workoutDoc.getString("description") ?: "",
                        description1 = workoutDoc.getString("description1") ?: ""
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
                                    endTime = sessionDoc.getString("endTime"),
                                    notes = sessionDoc.getString("notes")
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
                                                    reps = (it["reps"] as? Long)?.toInt() ?: 0
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
            batch.commit()
        }
    }
}

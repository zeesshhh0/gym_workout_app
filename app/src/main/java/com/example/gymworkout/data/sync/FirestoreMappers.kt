package com.example.gymworkout.data.sync

import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.model.Set
import com.example.gymworkout.data.model.Workout
import com.example.gymworkout.data.model.WorkoutSession

fun Workout.toFirestoreMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["id"] = id
    map["name"] = name
    return map
}

fun WorkoutSession.toFirestoreMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["sessionId"] = id
    map["workoutId"] = workoutId
    map["workoutName"] = workoutName
    map["date"] = date
    map["startTime"] = startTime ?: ""
    map["endTime"] = endTime ?: ""
    return map
}

fun Exercise.toFirestoreMap(sets: List<Set>): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["id"] = id
    map["name"] = name
    map["muscleGroup"] = muscleGroup
    map["sets"] = sets.map { it.toFirestoreMap() }
    return map
}

fun Set.toFirestoreMap(): HashMap<String, Any> {
    val map = HashMap<String, Any>()
    map["id"] = id
    map["sessionId"] = sessionId
    map["exerciseId"] = exerciseId
    map["setNumber"] = setNumber
    map["reps"] = reps
    map["weightUsed"] = weightUsed
    map["isCompleted"] = isCompleted
    return map
}

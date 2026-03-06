package com.example.gymworkout.ui.workout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.adapters.WorkoutAdapter
import com.example.gymworkout.ui.exercise.ExercisesActivity
import com.example.gymworkout.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date

class WorkoutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var buttonAddExercise: Button
    private lateinit var buttonFinishWorkout: Button
    private lateinit var editTextWorkoutName: EditText
    private lateinit var repository: WorkoutRepository
    private var workoutId: Long = -1
    private var sessionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

//        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)

        repository = WorkoutRepository(this)
        repository.setSyncListeners(
            onFailure = { message ->
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
            },
            onUnauthenticated = {
                redirectToLogin()
            }
        )

        workoutId = intent.getLongExtra("workoutId", -1)
        if (workoutId == -1L) {
            workoutId = repository.addWorkout("New Workout")
            sessionId = repository.getOrCreateWorkoutSession(workoutId.toInt())
        } else {
            sessionId = repository.getOrCreateWorkoutSession(workoutId.toInt())
        }

        editTextWorkoutName = findViewById(R.id.editTextWorkoutName)
        editTextWorkoutName.setText(repository.getWorkoutName(workoutId))
        editTextWorkoutName.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                repository.updateWorkoutName(workoutId, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        recyclerView = findViewById(R.id.recycler_view_workout_exercises)
        buttonAddExercise = findViewById(R.id.button_add_exercise)
        buttonFinishWorkout = findViewById(R.id.button_finish_workout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val exercises = repository.getExercisesForWorkout(workoutId.toInt())
        workoutAdapter = WorkoutAdapter(exercises, true) {
            refreshExercises()
        }
        recyclerView.adapter = workoutAdapter

        buttonAddExercise.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            intent.putExtra("workoutId", workoutId)
            startActivity(intent)
        }

        buttonFinishWorkout.setOnClickListener {
            finishWorkout()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refreshExercises()
    }

    private fun refreshExercises() {
        val exercises = repository.getExercisesForWorkout(workoutId.toInt())
        workoutAdapter.exercises = exercises
        workoutAdapter.notifyDataSetChanged()
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Finish or Delete Workout?")
            .setMessage("Do you want to finish or delete this workout?")
            .setPositiveButton("Finish") { _, _ ->
                finishWorkout()
            }
            .setNegativeButton("Delete") { _, _ ->
                deleteWorkout()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun finishWorkout() {
        val exercises = repository.getExercisesForWorkout(workoutId.toInt())
        if (exercises.isEmpty()) {
            repository.deleteWorkoutAndSession(workoutId, sessionId)
            setResult(RESULT_OK)
            finish()
            return
        }

        for (exercise in exercises) {
            val sets = repository.getSetsForExercise(sessionId, exercise.id)
            if (sets.isEmpty()) {
                showInvalidExerciseDialog(exercise.name)
                return
            }
            for (set in sets) {
                if (set.weightUsed == 0f && set.reps == 0) {
                    showInvalidExerciseDialog(exercise.name)
                    return
                }
            }
        }

        val timeFormat = SimpleDateFormat("HH:mm:ss")
        repository.updateWorkoutSessionEndTime(sessionId, timeFormat.format(Date()))
        setResult(RESULT_OK)
        finish()
    }

    private fun showInvalidExerciseDialog(exerciseName: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Incomplete Exercise")
            .setMessage("The exercise '$exerciseName' has no sets or contains empty sets (both weight and reps cannot be zero). Please add valid sets or delete the exercise.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun deleteWorkout() {
        repository.deleteWorkoutAndSession(workoutId, sessionId)
        setResult(RESULT_OK)
        finish()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

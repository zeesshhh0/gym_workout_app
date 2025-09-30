package com.example.gymworkout.ui.workout

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.ui.adapters.WorkoutAdapter
import com.example.gymworkout.ui.exercise.ExercisesActivity
import java.text.SimpleDateFormat
import java.util.Date

class WorkoutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var buttonAddExercise: Button
    private lateinit var buttonFinishWorkout: Button
    private lateinit var editTextWorkoutName: EditText
    private lateinit var dbHelper: DatabaseHelper
    private var workoutId: Long = -1
    private var sessionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        dbHelper = DatabaseHelper(this)

        workoutId = intent.getLongExtra("workoutId", -1)
        if (workoutId == -1L) {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)
            workoutId = dbHelper.addWorkout(userId, "New Workout", "")
            sessionId = dbHelper.getOrCreateWorkoutSession(workoutId.toInt())
        } else {
            sessionId = dbHelper.getOrCreateWorkoutSession(workoutId.toInt())
        }

        editTextWorkoutName = findViewById(R.id.editTextWorkoutName)
        editTextWorkoutName.setText(dbHelper.getWorkoutName(workoutId))
        editTextWorkoutName.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                dbHelper.updateWorkoutName(workoutId, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        recyclerView = findViewById(R.id.recycler_view_workout_exercises)
        buttonAddExercise = findViewById(R.id.button_add_exercise)
        buttonFinishWorkout = findViewById(R.id.button_finish_workout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val exercises = dbHelper.getExercisesForWorkout(workoutId.toInt())
        workoutAdapter = WorkoutAdapter(exercises, true)
        recyclerView.adapter = workoutAdapter

        buttonAddExercise.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            intent.putExtra("workoutId", workoutId)
            startActivity(intent)
        }

        buttonFinishWorkout.setOnClickListener {
            val exercises = dbHelper.getExercisesForWorkout(workoutId.toInt())
            if (exercises.isEmpty()) {
                dbHelper.deleteWorkoutAndSession(workoutId, sessionId)
            } else {
                val timeFormat = SimpleDateFormat("HH:mm:ss")
                dbHelper.updateWorkoutSessionEndTime(sessionId, timeFormat.format(Date()))
            }
            setResult(RESULT_OK)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val exercises = dbHelper.getExercisesForWorkout(workoutId.toInt())
        workoutAdapter.exercises = exercises
        workoutAdapter.notifyDataSetChanged()
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Finish or Delete Workout?")
        builder.setMessage("Do you want to finish or delete this workout?")

        builder.setPositiveButton("Finish") { _, _ ->
            finishWorkout()
        }

        builder.setNegativeButton("Delete") { _, _ ->
            deleteWorkout()
        }

        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun finishWorkout() {
        val exercises = dbHelper.getExercisesForWorkout(workoutId.toInt())
        if (exercises.isEmpty()) {
            dbHelper.deleteWorkoutAndSession(workoutId, sessionId)
        } else {
            val timeFormat = SimpleDateFormat("HH:mm:ss")
            dbHelper.updateWorkoutSessionEndTime(sessionId, timeFormat.format(Date()))
        }
        setResult(RESULT_OK)
        finish()
    }

    private fun deleteWorkout() {
        dbHelper.deleteWorkoutAndSession(workoutId, sessionId)
        setResult(RESULT_OK)
        finish()
    }
}

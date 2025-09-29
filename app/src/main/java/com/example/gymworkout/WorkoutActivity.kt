package com.example.gymworkout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.data.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.ui.WorkoutAdapter

class WorkoutActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var buttonAddExercise: Button
    private lateinit var buttonFinishWorkout: Button
    private lateinit var dbHelper: DatabaseHelper
    private var workoutId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        dbHelper = DatabaseHelper(this)

        workoutId = intent.getLongExtra("workoutId", -1)
        if (workoutId == -1L) {
            workoutId = dbHelper.addWorkout(1, "New Workout", "")
        }

        recyclerView = findViewById(R.id.recycler_view_workout_exercises)
        buttonAddExercise = findViewById(R.id.button_add_exercise)
        buttonFinishWorkout = findViewById(R.id.button_finish_workout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val exercises = dbHelper.getExercisesForWorkout(workoutId.toInt())
        workoutAdapter = WorkoutAdapter(exercises)
        recyclerView.adapter = workoutAdapter

        buttonAddExercise.setOnClickListener {
            val intent = Intent(this, ExercisesActivity::class.java)
            intent.putExtra("workoutId", workoutId)
            startActivity(intent)
        }

        buttonFinishWorkout.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val exercises = dbHelper.getExercisesForWorkout(workoutId.toInt())
        workoutAdapter.exercises = exercises
        workoutAdapter.notifyDataSetChanged()
    }
}

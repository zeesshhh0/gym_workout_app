package com.example.gymworkout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.data.DatabaseHelper

class AddSetActivity : AppCompatActivity() {

    private lateinit var editTextSets: EditText
    private lateinit var editTextReps: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var buttonAddSet: Button
    private lateinit var dbHelper: DatabaseHelper
    private var workoutId: Int = -1
    private var exerciseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_set)

        dbHelper = DatabaseHelper(this)

        workoutId = intent.getIntExtra("workoutId", -1)
        exerciseId = intent.getIntExtra("exerciseId", -1)

        editTextSets = findViewById(R.id.edit_text_sets)
        editTextReps = findViewById(R.id.edit_text_reps)
        editTextWeight = findViewById(R.id.edit_text_weight)
        buttonAddSet = findViewById(R.id.button_add_set)

        buttonAddSet.setOnClickListener {
            val sets = editTextSets.text.toString().toInt()
            val reps = editTextReps.text.toString().toInt()
            val weight = editTextWeight.text.toString().toFloat()

            dbHelper.addSet(workoutId, exerciseId, sets, reps, weight)

            finish()
        }
    }
}

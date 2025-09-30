package com.example.gymworkout.ui.workout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper

class AddSetActivity : AppCompatActivity() {

    private lateinit var textViewSetNumber: TextView
    private lateinit var editTextReps: EditText
    private lateinit var editTextWeight: EditText
    private lateinit var buttonAddSet: Button
    private lateinit var dbHelper: DatabaseHelper
    private var workoutId: Int = -1
    private var exerciseId: Int = -1
    private var setNumber: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_set)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Set"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        workoutId = intent.getIntExtra("workoutId", -1)
        exerciseId = intent.getIntExtra("exerciseId", -1)
        setNumber = intent.getIntExtra("setNumber", -1)

        textViewSetNumber = findViewById(R.id.text_view_set_number)
        editTextReps = findViewById(R.id.edit_text_reps)
        editTextWeight = findViewById(R.id.edit_text_weight)
        buttonAddSet = findViewById(R.id.button_add_set)

        textViewSetNumber.text = "Set: $setNumber"

        buttonAddSet.setOnClickListener {
            val reps = editTextReps.text.toString().toInt()
            val weight = editTextWeight.text.toString().toFloat()

            dbHelper.addSet(workoutId, exerciseId, setNumber, reps, weight)

            finish()
        }
    }
}

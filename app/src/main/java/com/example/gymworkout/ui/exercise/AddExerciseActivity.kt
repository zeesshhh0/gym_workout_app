package com.example.gymworkout.ui.exercise

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.MuscleGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var muscleGroups: List<MuscleGroup>
    private var selectedMuscleGroupId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Add Exercise"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val exerciseNameEditText = findViewById<TextInputEditText>(R.id.exerciseNameEditText)
        val exerciseDescriptionEditText = findViewById<TextInputEditText>(R.id.exerciseDescriptionEditText)
        val exerciseInstructionsEditText = findViewById<TextInputEditText>(R.id.exerciseInstructionsEditText)
        val muscleGroupAutoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.muscleGroupAutoCompleteTextView)
        val saveExerciseButton = findViewById<MaterialButton>(R.id.saveExerciseButton)

        val dbHelper = DatabaseHelper(this)
        muscleGroups = dbHelper.getAllMuscleGroups()
        val muscleGroupNames = muscleGroups.map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, muscleGroupNames)
        muscleGroupAutoCompleteTextView.setAdapter(adapter)

        muscleGroupAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            selectedMuscleGroupId = muscleGroups[position].id
        }

        saveExerciseButton.setOnClickListener {
            val name = exerciseNameEditText.text.toString()
            val description = exerciseDescriptionEditText.text.toString()
            val instructions = exerciseInstructionsEditText.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && instructions.isNotEmpty() && selectedMuscleGroupId != -1) {
                val id = dbHelper.addExercise(selectedMuscleGroupId, name, description, instructions)
                if (id != -1L) {
                    Toast.makeText(this, "Exercise added successfully", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Error adding exercise", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields and select a muscle group", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
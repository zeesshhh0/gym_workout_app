package com.example.gymworkout.ui.exercise

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddExerciseActivity : AppCompatActivity() {

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
        val saveExerciseButton = findViewById<MaterialButton>(R.id.saveExerciseButton)

        val dbHelper = DatabaseHelper(this)

        saveExerciseButton.setOnClickListener {
            val name = exerciseNameEditText.text.toString()
            val description = exerciseDescriptionEditText.text.toString()
            val instructions = exerciseInstructionsEditText.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && instructions.isNotEmpty()) {
                val id = dbHelper.addExercise(1, name, description, instructions) // Assuming muscle_group_id 1 for now
                if (id != -1L) {
                    Toast.makeText(this, "Exercise added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error adding exercise", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
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
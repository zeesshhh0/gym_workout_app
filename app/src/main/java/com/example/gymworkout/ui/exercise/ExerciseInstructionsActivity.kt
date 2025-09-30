package com.example.gymworkout.ui.exercise

import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

class ExerciseInstructionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_instructions)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val exerciseName = intent.getStringExtra("exercise_name")

        supportActionBar?.title = exerciseName

        val dbHelper = DatabaseHelper(this)
        val instructions = exerciseName?.let { dbHelper.getExerciseInstructions(it) }

        val exerciseInstructionsTextView = findViewById<MaterialTextView>(R.id.exerciseInstructionsTextView)

        val formattedInstructions = instructions?.let { formatAsOrderedList(it) }
        exerciseInstructionsTextView.text = Html.fromHtml(formattedInstructions, Html.FROM_HTML_MODE_LEGACY)


    }
    private fun formatAsOrderedList(instructions: String): String {
        val steps = instructions.split(Regex("(?=\\d+\\.)"))
            .filter { it.isNotBlank() }
            .map { it.trim() }

        return buildString {
            append("<h4>Instructions</h4>")
            steps.forEach { step ->
                append("<p style=\"margin-bottom: 10px;\"><b>$step</b></p>") // Stack as paragraphs with spacing
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
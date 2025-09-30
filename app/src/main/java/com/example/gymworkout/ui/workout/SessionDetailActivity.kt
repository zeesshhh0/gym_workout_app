package com.example.gymworkout.ui.workout

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.ui.adapters.SessionDetailExerciseAdapter

class SessionDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private var sessionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Session Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        sessionId = intent.getIntExtra("session_id", -1)

        recyclerView = findViewById(R.id.sessionDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (sessionId != -1) {
            // Fetch exercises for this session
            val exercises = dbHelper.getExercisesForSession(sessionId)
            val sessionDetailExerciseAdapter = SessionDetailExerciseAdapter(exercises, sessionId, dbHelper, false)
            recyclerView.adapter = sessionDetailExerciseAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_session_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_delete_session -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Session")
            .setMessage("Are you sure you want to delete this workout session? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteSession()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSession() {
        dbHelper.deleteWorkoutSession(sessionId)
        setResult(RESULT_OK)
        finish() // Close the activity after deletion
    }
}
package com.example.gymworkout.ui.workout

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.adapters.SessionDetailExerciseAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class SessionDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var repository: WorkoutRepository
    private lateinit var workoutTitle: TextView
    private lateinit var workoutDateTime: TextView
    private lateinit var durationFooter: TextView
    private lateinit var volumeFooter: TextView
    private var sessionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_detail)

        val toolbar: com.google.android.material.appbar.MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = WorkoutRepository(this)
        sessionId = intent.getIntExtra("session_id", -1)

        workoutTitle = findViewById(R.id.workout_title)
        workoutDateTime = findViewById(R.id.workout_date_time)
        durationFooter = findViewById(R.id.duration_footer)
        volumeFooter = findViewById(R.id.volume_footer)

        recyclerView = findViewById(R.id.sessionDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (sessionId != -1) {
            setupUI()
        }
    }

    private fun setupUI() {
        val sessions = repository.getAllWorkoutSessions()
        val session = sessions.find { it.id == sessionId }
        
        if (session != null) {
            workoutTitle.text = session.workoutName
            
            // Format: "Thursday, March 5, 2026 at 2:58 PM"
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
            val inputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            
            val formattedDate = try {
                val date = inputDateFormat.parse(session.date)
                val baseDate = outputDateFormat.format(date ?: java.util.Date())
                val time = session.startTime?.let { 
                    val timeDate = inputTimeFormat.parse(it)
                    outputTimeFormat.format(timeDate ?: java.util.Date())
                } ?: ""
                "$baseDate at $time"
            } catch (e: Exception) {
                session.date
            }
            workoutDateTime.text = formattedDate
            
            val stats = repository.getSessionStats(sessionId)
            durationFooter.text = stats.getDurationText()
            volumeFooter.text = "${stats.totalVolume.toInt()} kg"
            
            val exercises = repository.getExercisesForSession(sessionId)
            val sessionDetailExerciseAdapter = SessionDetailExerciseAdapter(exercises, sessionId, repository)
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
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Session")
            .setMessage("Are you sure you want to delete this workout session? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteSession()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSession() {
        repository.deleteWorkoutSession(sessionId)
        setResult(RESULT_OK)
        finish() // Close the activity after deletion
    }
}

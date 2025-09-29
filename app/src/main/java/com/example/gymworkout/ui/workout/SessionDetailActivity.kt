package com.example.gymworkout.ui.workout

import android.os.Bundle
import android.view.MenuItem
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
        sessionId = intent.getIntExtra("sessionId", -1)

        recyclerView = findViewById(R.id.sessionDetailRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (sessionId != -1) {
            // Fetch exercises for this session
            val exercises = dbHelper.getExercisesForSession(sessionId)
            val sessionDetailExerciseAdapter = SessionDetailExerciseAdapter(exercises, sessionId, dbHelper)
            recyclerView.adapter = sessionDetailExerciseAdapter
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
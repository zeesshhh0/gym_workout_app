package com.example.gymworkout.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.WorkoutSession
import com.example.gymworkout.ui.adapters.SessionAdapter
import com.example.gymworkout.ui.workout.SessionDetailActivity

class SessionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var sessions: List<WorkoutSession> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        dbHelper = DatabaseHelper(requireContext())
        sessions = dbHelper.getAllWorkoutSessions()

        recyclerView = view.findViewById(R.id.sessionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        sessionAdapter = SessionAdapter(sessions) { session ->
            val intent = Intent(activity, SessionDetailActivity::class.java)
            intent.putExtra("sessionId", session.id)
            startActivity(intent)
        }
        recyclerView.adapter = sessionAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        sessions = dbHelper.getAllWorkoutSessions()
        sessionAdapter.updateData(sessions)
    }
}
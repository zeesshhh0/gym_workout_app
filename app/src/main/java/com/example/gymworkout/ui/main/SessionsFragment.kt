package com.example.gymworkout.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.HistoryItem
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.adapters.HistoryAdapter
import com.example.gymworkout.ui.workout.SessionDetailActivity

class SessionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noSessionsTextView: android.widget.TextView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var repository: WorkoutRepository
    private var historyItems: List<HistoryItem> = emptyList()

    private val sessionDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshSessions()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sessions, container, false)

        repository = WorkoutRepository(requireContext())
        noSessionsTextView = view.findViewById(R.id.noSessionsTextView)
        
        recyclerView = view.findViewById(R.id.sessionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        historyAdapter = HistoryAdapter(historyItems) { sessionId ->
            val intent = Intent(activity, SessionDetailActivity::class.java)
            intent.putExtra("session_id", sessionId)
            sessionDetailLauncher.launch(intent)
        }
        recyclerView.adapter = historyAdapter

        refreshSessions()

        return view
    }

    private fun refreshSessions() {
        historyItems = repository.getHistoryData()
        if (::noSessionsTextView.isInitialized) {
            noSessionsTextView.visibility = if (historyItems.isEmpty()) View.VISIBLE else View.GONE
        }
        if (::historyAdapter.isInitialized) {
            historyAdapter.updateData(historyItems)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshSessions()
    }
}
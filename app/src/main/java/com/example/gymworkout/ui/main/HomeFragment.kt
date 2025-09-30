package com.example.gymworkout.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.ui.workout.SessionDetailActivity
import com.example.gymworkout.ui.workout.WorkoutActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        dbHelper = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { updateUI(it) }
    }

    private fun updateUI(view: View) {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) return

        // Greeting
        val greetingTextView = view.findViewById<TextView>(R.id.text_view_greeting)
        val userName = dbHelper.getUserName(userId) ?: "User"
        greetingTextView.text = getGreeting(userName)

        // Streak
        val streakTextView = view.findViewById<TextView>(R.id.text_view_streak)
        val streak = dbHelper.calculateWorkoutStreak(userId)
        streakTextView.text = if (streak > 0) "$streak-day streak 🔥" else "Start a new workout!"

        val startWorkoutButton = view.findViewById<Button>(R.id.button_start_workout)
        startWorkoutButton.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            activity?.startActivity(intent)
        }

        // Last Workout
        val lastWorkoutCard = view.findViewById<View>(R.id.card_last_workout)
        val lastWorkoutName = view.findViewById<TextView>(R.id.text_view_last_workout_name)
        val lastWorkoutDate = view.findViewById<TextView>(R.id.text_view_last_workout_date)
        val lastWorkoutStats = view.findViewById<TextView>(R.id.text_view_last_workout_stats)
        val viewDetailsButton = view.findViewById<Button>(R.id.button_view_details)
        val lastSession = dbHelper.getLatestWorkoutSession(userId)

        if (lastSession != null) {
            lastWorkoutCard.visibility = View.VISIBLE
            lastWorkoutName.text = lastSession.workoutName
            lastWorkoutDate.text = getRelativeDate(lastSession.date)

            val stats = dbHelper.getSessionStats(lastSession.id)
            lastWorkoutStats.text = "⏱\uFE0F ${stats.getDurationText()} • \uD83C\uDFC3\u200D♂\uFE0F ${stats.exerciseCount} exercises • \uD83C\uDFCB\uFE0F ${String.format("%,.0f", stats.totalVolume)} kg volume"

            viewDetailsButton.setOnClickListener {
                val intent = Intent(activity, SessionDetailActivity::class.java)
                intent.putExtra("session_id", lastSession.id)
                startActivity(intent)
            }
        } else {
            lastWorkoutCard.visibility = View.GONE
        }
    }

    private fun getGreeting(name: String): String {
        val c = Calendar.getInstance()
        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning, $name!"
            in 12..17 -> "Good Afternoon, $name!"
            in 17..23 -> "Good Evening, $name!"
            else -> "Hello, $name!"
        }
    }

    private fun getRelativeDate(dateStr: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val workoutDate = Calendar.getInstance()
        workoutDate.time = sdf.parse(dateStr)!!

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            today.get(Calendar.YEAR) == workoutDate.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == workoutDate.get(Calendar.DAY_OF_YEAR) -> "Today"
            yesterday.get(Calendar.YEAR) == workoutDate.get(Calendar
.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == workoutDate.get(Calendar.DAY_OF_YEAR) -> "Yesterday"
            else -> dateStr
        }
    }
}
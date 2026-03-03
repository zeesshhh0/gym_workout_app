package com.example.gymworkout.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.workout.SessionDetailActivity
import com.example.gymworkout.ui.workout.WorkoutActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var repository: WorkoutRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        repository = WorkoutRepository(requireContext())
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
        // Greeting
        val greetingTextView = view.findViewById<TextView>(R.id.text_view_greeting)
        val userName = repository.getUserName() ?: "User"
        greetingTextView.text = getGreeting(userName)

        // Streak
        val streakCountText = view.findViewById<TextView>(R.id.text_view_streak_count)
        val streakUnitText = view.findViewById<TextView>(R.id.text_view_streak_unit)
        val streakProgress = view.findViewById<CircularProgressIndicator>(R.id.progress_streak_circular)
        val streakSubtext = view.findViewById<TextView>(R.id.text_view_streak_subtext)
        
        val streak = repository.calculateWorkoutStreak()
        streakCountText.text = streak.toString()
        streakUnitText.text = if (streak == 1) "Day" else "Days"
        
        // Mocking a weekly goal of 4 workouts for the progress indicator
        val weeklyGoal = 4
        val progressValue = ((streak % weeklyGoal).toFloat() / weeklyGoal * 100).toInt()
        streakProgress.progress = if (streak > 0 && progressValue == 0) 100 else progressValue
        
        val daysToGo = weeklyGoal - (streak % weeklyGoal)
        streakSubtext.text = if (daysToGo > 0) "$daysToGo days to go until your weekly goal." else "Goal reached! Keep it up!"

        val startWorkoutButton = view.findViewById<ExtendedFloatingActionButton>(R.id.button_start_workout)
        startWorkoutButton.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            activity?.startActivity(intent)
        }

        // Last Workout
        val lastWorkoutCard = view.findViewById<View>(R.id.card_last_workout)
        val lastWorkoutHeader = view.findViewById<View>(R.id.text_view_last_workout_header)
        val lastWorkoutName = view.findViewById<TextView>(R.id.text_view_last_workout_name)
        val lastWorkoutDate = view.findViewById<TextView>(R.id.text_view_last_workout_date)
        
        val durationText = view.findViewById<TextView>(R.id.text_view_duration)
        val exerciseCountText = view.findViewById<TextView>(R.id.text_view_exercise_count)
        val volumeText = view.findViewById<TextView>(R.id.text_view_volume)
        
        val viewDetailsButton = view.findViewById<MaterialButton>(R.id.button_view_details)
        val lastSession = repository.getLatestWorkoutSession()

        if (lastSession != null) {
            lastWorkoutHeader.visibility = View.VISIBLE
            lastWorkoutCard.visibility = View.VISIBLE
            lastWorkoutName.text = lastSession.workoutName
            lastWorkoutDate.text = getRelativeDate(lastSession.date)

            val stats = repository.getSessionStats(lastSession.id)
            durationText.text = stats.getDurationText()
            exerciseCountText.text = "${stats.exerciseCount} exercises"
            volumeText.text = "${String.format("%,.0f", stats.totalVolume)} kg"

            viewDetailsButton.setOnClickListener {
                val intent = Intent(activity, SessionDetailActivity::class.java)
                intent.putExtra("session_id", lastSession.id)
                startActivity(intent)
            }
        } else {
            lastWorkoutHeader.visibility = View.GONE
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
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val workoutDate = Calendar.getInstance()
            workoutDate.time = sdf.parse(dateStr)!!

            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

            when {
                today.get(Calendar.YEAR) == workoutDate.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == workoutDate.get(Calendar.DAY_OF_YEAR) -> "Today"
                yesterday.get(Calendar.YEAR) == workoutDate.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == workoutDate.get(Calendar.DAY_OF_YEAR) -> "Yesterday"
                else -> dateStr
            }
        } catch (e: Exception) {
            dateStr
        }
    }
}
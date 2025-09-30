package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.WorkoutSession
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SessionAdapter(private var sessions: List<WorkoutSession>, private val clickListener: (WorkoutSession) -> Unit) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.textViewWorkoutName)
        val sessionDate: TextView = itemView.findViewById(R.id.textViewSessionDate)
        val sessionDuration: TextView = itemView.findViewById(R.id.textViewSessionDuration)

        fun bind(session: WorkoutSession, clickListener: (WorkoutSession) -> Unit) {
            itemView.setOnClickListener { clickListener(session) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessions[position]

        // Format date as "Wed, September 12, 2025"
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("EEE, MMMM d, yyyy", Locale.getDefault())
        val formattedDate = try {
            val date = inputDateFormat.parse(session.date)
            "Date: ${outputDateFormat.format(date)}"
        } catch (e: Exception) {
            "Date: ${session.date}" // Fallback if parsing fails
        }
        holder.sessionDate.text = formattedDate

        // Format workout name
        holder.workoutName.text = session.workoutName

        // Calculate and format duration
        val durationText = if (session.startTime != null && session.endTime != null) {
            try {
                val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")
                val start = LocalTime.parse(session.startTime, timeFormat)
                val end = LocalTime.parse(session.endTime, timeFormat)

                // Calculate duration in minutes
                val duration = java.time.Duration.between(start, end)
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60

                // Format duration as "X hour(s) Y minute(s)"
                when {
                    hours > 0 && minutes > 0 -> "Duration: $hours hour${if (hours > 1) "s" else ""} $minutes minute${if (minutes > 1) "s" else ""}"
                    hours > 0 -> "Duration: $hours hour${if (hours > 1) "s" else ""}"
                    minutes > 0 -> "Duration: $minutes minute${if (minutes > 1) "s" else ""}"
                    else -> "Duration: 0 minutes"
                }
            } catch (e: Exception) {
                "Duration: N/A" // Fallback if parsing fails
            }
        } else if (session.startTime != null) {
            "Start Time: ${session.startTime}"
        } else {
            "Duration: N/A"
        }
        holder.sessionDuration.text = durationText

        holder.bind(session, clickListener)
    }

    override fun getItemCount(): Int = sessions.size

    fun updateData(newSessions: List<WorkoutSession>) {
        sessions = newSessions
        notifyDataSetChanged()
    }
}
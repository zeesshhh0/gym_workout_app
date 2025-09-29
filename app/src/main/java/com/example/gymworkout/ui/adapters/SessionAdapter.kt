package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.WorkoutSession

import android.content.Intent
import com.example.gymworkout.ui.workout.SessionDetailActivity

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
        holder.workoutName.text = session.workoutName
        holder.sessionDate.text = "Date: ${session.date}"

        val durationText = if (session.startTime != null && session.endTime != null) {
            "Duration: ${session.startTime} - ${session.endTime}"
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
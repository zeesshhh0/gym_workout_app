package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise

class SessionDetailExerciseAdapter(
    private var exercises: List<Exercise>,
    private val sessionId: Int,
    private val dbHelper: DatabaseHelper,
    private val showAddSetButton: Boolean = false
) : RecyclerView.Adapter<SessionDetailExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.text_view_exercise_name)
        val muscleGroup: TextView = itemView.findViewById(R.id.text_view_muscle_group)
        val setsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view_sets)
        val addSetButton: android.widget.Button = itemView.findViewById(R.id.button_add_set)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name
        holder.muscleGroup.text = exercise.muscleGroup

        if (showAddSetButton) {
            holder.addSetButton.visibility = View.VISIBLE
        } else {
            holder.addSetButton.visibility = View.GONE
        }

        // Fetch sets for this exercise in this session
        val sets = dbHelper.getSetsForExerciseInSession(sessionId, exercise.id)
        val setAdapter = SetAdapter(sets)
        holder.setsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.setsRecyclerView.adapter = setAdapter
    }

    override fun getItemCount(): Int = exercises.size

    fun updateData(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}
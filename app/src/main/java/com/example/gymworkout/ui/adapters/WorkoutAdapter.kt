package com.example.gymworkout.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.ui.workout.AddSetActivity

class WorkoutAdapter(var exercises: List<Exercise>, private val showAddSetButton: Boolean = true) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.name
        holder.muscleGroup.text = exercise.muscleGroup

        if (showAddSetButton) {
            holder.addSetButton.visibility = View.VISIBLE
        } else {
            holder.addSetButton.visibility = View.GONE
        }

        val dbHelper = DatabaseHelper(holder.itemView.context)
        val sessionId = dbHelper.getOrCreateWorkoutSession(exercise.workoutId)
        val sets = dbHelper.getSetsForExercise(sessionId, exercise.id)
        val setAdapter = SetAdapter(sets)
        holder.setsRecyclerView.adapter = setAdapter
        holder.setsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

        holder.addSetButton.setOnClickListener {
            val nextSetNumber = sets.size + 1
            val intent = Intent(holder.itemView.context, AddSetActivity::class.java)
            intent.putExtra("workoutId", exercise.workoutId)
            intent.putExtra("exerciseId", exercise.id)
            intent.putExtra("setNumber", nextSetNumber)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_view_exercise_name)
        val muscleGroup: TextView = itemView.findViewById(R.id.text_view_muscle_group)
        val setsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view_sets)
        val addSetButton: android.widget.Button = itemView.findViewById(R.id.button_add_set)
    }
}

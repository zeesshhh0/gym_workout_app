package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.repository.WorkoutRepository

class SessionDetailExerciseAdapter(
    private var exercises: List<Exercise>,
    private val sessionId: Int,
    private val repository: WorkoutRepository
) : RecyclerView.Adapter<SessionDetailExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.exercise_name)
        val setListContainer: LinearLayout = itemView.findViewById(R.id.set_list_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseName.text = exercise.name

        // Fetch sets for this exercise in this session
        val sets = repository.getSetsForExerciseInSession(sessionId, exercise.id)
        
        holder.setListContainer.removeAllViews()
        val inflater = LayoutInflater.from(holder.itemView.context)
        
        sets.forEach { set ->
            val setRow = inflater.inflate(R.layout.item_detail_set_row, holder.setListContainer, false)
            val setNumber: TextView = setRow.findViewById(R.id.set_number)
            val setData: TextView = setRow.findViewById(R.id.set_data)
            
            setNumber.text = set.setNumber.toString()
            setData.text = "${set.weightUsed.toInt()} kg x ${set.reps}"
            
            holder.setListContainer.addView(setRow)
        }
    }

    override fun getItemCount(): Int = exercises.size

    fun updateData(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }
}

package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise

class ExerciseRecyclerAdapter(
    private var exercises: List<Exercise>,
    private val onItemClick: (Exercise) -> Unit,
    private val showAddSetButton: Boolean = false
) : RecyclerView.Adapter<ExerciseRecyclerAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
        if (showAddSetButton) {
            holder.addSetButton.visibility = View.VISIBLE
        } else {
            holder.addSetButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = exercises.size

    fun updateData(newExercises: List<Exercise>) {
        exercises = newExercises
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*private val exerciseImageView: ImageView = itemView.findViewById(R.id.text_view_exercise_name)*/
        private val exerciseNameTextView: TextView = itemView.findViewById(R.id.text_view_exercise_name)
        private val muscleGroupTextView: TextView = itemView.findViewById(R.id.text_view_muscle_group)
        val addSetButton: android.widget.Button = itemView.findViewById(R.id.button_add_set)

        fun bind(exercise: Exercise) {
            exerciseNameTextView.text = exercise.name
            muscleGroupTextView.text = exercise.muscleGroup

           /* val drawableId = when (exercise.muscleGroup.lowercase()) {
                "chest" -> R.drawable.chest
                "biceps" -> R.drawable.bicpes
                "quadriceps" -> R.drawable.quadriceps
                "abs" -> R.drawable.abs
                else -> R.drawable.bicpes
            }
            exerciseImageView.setImageResource(drawableId)*/

            itemView.setOnClickListener { onItemClick(exercise) }
        }
    }
}
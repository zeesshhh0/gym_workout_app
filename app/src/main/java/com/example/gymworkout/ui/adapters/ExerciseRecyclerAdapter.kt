package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise

class ExerciseRecyclerAdapter(
    private val onItemClick: (Exercise) -> Unit,
    private val showAddSetButton: Boolean = false,
    private val showExerciseImage: Boolean = true
) : ListAdapter<Exercise, ExerciseRecyclerAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = getItem(position)
        holder.bind(exercise)
        if (showAddSetButton) {
            holder.addSetButton.visibility = View.VISIBLE
        } else {
            holder.addSetButton.visibility = View.GONE
        }
        if (showExerciseImage) {
            holder.iconContainer.visibility = View.VISIBLE
        } else {
            holder.iconContainer.visibility = View.GONE
        }
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconContainer: android.view.View = itemView.findViewById(R.id.icon_container)
        private val exerciseImageView: android.widget.ImageView = itemView.findViewById(R.id.image_view_exercise)
        private val exerciseNameTextView: TextView = itemView.findViewById(R.id.text_view_exercise_name)
        private val muscleGroupTextView: TextView = itemView.findViewById(R.id.text_view_muscle_group)
        val addSetButton: android.widget.Button = itemView.findViewById(R.id.button_add_set)

        fun bind(exercise: Exercise) {
            exerciseNameTextView.text = exercise.name
            muscleGroupTextView.text = exercise.muscleGroup

            val drawableId = when (exercise.muscleGroup.lowercase()) {
                "chest" -> R.drawable.chest
                "biceps" -> R.drawable.biceps
                "legs" -> R.drawable.quadriceps
                else -> R.drawable.exercise
            }
            exerciseImageView.setImageResource(drawableId)
            
            // Remove tint for muscle group images if they are colored pngs
            if (drawableId != R.drawable.exercise) {
                exerciseImageView.imageTintList = null
            } else {
                // Restore tint for generic icon
                val colorStateList = itemView.context.getColorStateList(R.color.md_theme_dark_onPrimary)
                exerciseImageView.imageTintList = colorStateList
            }

            itemView.setOnClickListener { onItemClick(exercise) }
        }
    }

    class ExerciseDiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }
}

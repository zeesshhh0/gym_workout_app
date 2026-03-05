package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.repository.WorkoutRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WorkoutAdapter(
    var exercises: List<Exercise>, 
    private val showAddSetButton: Boolean = true,
    private val onExerciseDeleted: () -> Unit = {}
) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    private lateinit var repository: WorkoutRepository

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.name
        holder.muscleGroup.text = exercise.muscleGroup

        if (showAddSetButton) {
            holder.addSetButton.visibility = View.VISIBLE
            holder.deleteExerciseButton.visibility = View.VISIBLE
        } else {
            holder.addSetButton.visibility = View.GONE
            holder.deleteExerciseButton.visibility = View.GONE
        }

        if (!::repository.isInitialized) {
            repository = WorkoutRepository(holder.itemView.context)
        }
        val sessionId = repository.getOrCreateWorkoutSession(exercise.workoutId)
        val sets = repository.getSetsForExercise(sessionId, exercise.id)
        
        if (holder.setsRecyclerView.adapter == null) {
            val setAdapter = SetAdapter(sets, { set, newReps, newWeight ->
                repository.updateSet(set.id, newReps, newWeight)
            }, { set ->
                showDeleteSetDialog(holder.itemView.context, set) {
                    val updatedSets = repository.getSetsForExercise(sessionId, exercise.id)
                    (holder.setsRecyclerView.adapter as SetAdapter).updateData(updatedSets)
                }
            }, { set, isCompleted ->
                repository.updateSetCompletionStatus(set.id, isCompleted)
                val updatedSets = repository.getSetsForExercise(sessionId, exercise.id)
                (holder.setsRecyclerView.adapter as SetAdapter).updateData(updatedSets)
            })
            holder.setsRecyclerView.adapter = setAdapter
            holder.setsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        } else {
            (holder.setsRecyclerView.adapter as SetAdapter).updateData(sets)
        }

        holder.addSetButton.setOnClickListener {
            val currentSets = (holder.setsRecyclerView.adapter as SetAdapter).sets
            val nextSetNumber = currentSets.size + 1
            repository.addSet(exercise.workoutId, exercise.id, nextSetNumber, 0, 0f)
            val updatedSets = repository.getSetsForExercise(sessionId, exercise.id)
            (holder.setsRecyclerView.adapter as SetAdapter).updateData(updatedSets)
        }

        holder.deleteExerciseButton.setOnClickListener {
            showDeleteExerciseDialog(holder.itemView.context, exercise.name) {
                repository.deleteExerciseFromWorkout(exercise.workoutId, sessionId, exercise.id)
                onExerciseDeleted()
            }
        }
    }

    private fun showDeleteExerciseDialog(context: android.content.Context, exerciseName: String, onExerciseDeleted: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Exercise")
            .setMessage("Are you sure you want to delete '$exerciseName' and all its sets from this workout?")
            .setPositiveButton("Delete") { _, _ ->
                onExerciseDeleted()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteSetDialog(context: android.content.Context, set: com.example.gymworkout.data.model.Set, onSetDeleted: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Set")
            .setMessage("Are you sure you want to delete this set?")
            .setPositiveButton("Delete") { _, _ ->
                repository.deleteSet(set.id)
                onSetDeleted()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_view_exercise_name)
        val muscleGroup: TextView = itemView.findViewById(R.id.text_view_muscle_group)
        val setsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view_sets)
        val addSetButton: android.widget.Button = itemView.findViewById(R.id.button_add_set)
        val deleteExerciseButton: View = itemView.findViewById(R.id.button_delete_exercise)
    }
}

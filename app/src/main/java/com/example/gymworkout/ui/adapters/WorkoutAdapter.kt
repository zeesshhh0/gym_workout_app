package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise

class WorkoutAdapter(var exercises: List<Exercise>, private val showAddSetButton: Boolean = true) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.name
        holder.muscleGroup.text = exercise.muscleGroup
        holder.exerciseImageView.visibility = View.GONE

        if (showAddSetButton) {
            holder.addSetButton.visibility = View.VISIBLE
        } else {
            holder.addSetButton.visibility = View.GONE
        }

        val dbHelper = DatabaseHelper(holder.itemView.context)
        val sessionId = dbHelper.getOrCreateWorkoutSession(exercise.workoutId)
        var sets = dbHelper.getSetsForExercise(sessionId, exercise.id)
        val setAdapter = SetAdapter(sets, { set ->
            showEditSetDialog(holder.itemView.context, set) {
                // Refresh sets after editing
                val updatedSets = dbHelper.getSetsForExercise(sessionId, exercise.id)
                (holder.setsRecyclerView.adapter as SetAdapter).updateData(updatedSets)
            }
        }, { set ->
            showDeleteSetDialog(holder.itemView.context, set) {
                // Refresh sets after deleting
                val updatedSets = dbHelper.getSetsForExercise(sessionId, exercise.id)
                (holder.setsRecyclerView.adapter as SetAdapter).updateData(updatedSets)
            }
        })
        holder.setsRecyclerView.adapter = setAdapter
        holder.setsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)

        holder.addSetButton.setOnClickListener {
            val nextSetNumber = sets.size + 1
            dbHelper.addSet(exercise.workoutId, exercise.id, nextSetNumber, 0, 0f)
            // Refresh sets
            sets = dbHelper.getSetsForExercise(sessionId, exercise.id)
            (holder.setsRecyclerView.adapter as SetAdapter).updateData(sets)
        }
    }

    private fun showDeleteSetDialog(context: android.content.Context, set: com.example.gymworkout.data.model.Set, onSetDeleted: () -> Unit) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Delete Set")
        builder.setMessage("Are you sure you want to delete this set?")
        builder.setPositiveButton("Delete") { _, _ ->
            val dbHelper = DatabaseHelper(context)
            dbHelper.deleteSet(set.id)
            onSetDeleted()
        }
        builder.setNegativeButton("Cancel", null)
        builder.create().show()
    }

    private fun showEditSetDialog(context: android.content.Context, set: com.example.gymworkout.data.model.Set, onSetUpdated: () -> Unit) {
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Edit Set")

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_set, null)
        val repsEditText = view.findViewById<android.widget.EditText>(R.id.edit_text_reps)
        val weightEditText = view.findViewById<android.widget.EditText>(R.id.edit_text_weight)

        repsEditText.setText(set.reps.toString())
        weightEditText.setText(set.weightUsed.toString())

        builder.setView(view)

        builder.setPositiveButton("Save") { _, _ ->
            val newReps = repsEditText.text.toString().toIntOrNull() ?: set.reps
            val newWeight = weightEditText.text.toString().toFloatOrNull() ?: set.weightUsed
            val dbHelper = DatabaseHelper(context)
            dbHelper.updateSet(set.id, newReps, newWeight)
            onSetUpdated()
        }
        builder.setNegativeButton("Cancel", null)

        builder.create().show()
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text_view_exercise_name)
        val muscleGroup: TextView = itemView.findViewById(R.id.text_view_muscle_group)
        val exerciseImageView: ImageView = itemView.findViewById(R.id.image_view_exercise)
        val setsRecyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view_sets)
        val addSetButton: android.widget.Button = itemView.findViewById(R.id.button_add_set)
    }
}

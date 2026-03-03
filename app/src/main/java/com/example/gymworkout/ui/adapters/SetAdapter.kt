package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Set

class SetAdapter(
    var sets: List<Set>,
    private val onSetUpdate: (Set, Int, Float) -> Unit,
    private val onSetDeleteClick: (Set) -> Unit,
    private val onSetCompleteChange: (Set, Boolean) -> Unit
) : RecyclerView.Adapter<SetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val set = sets[position]
        holder.setNumber.text = "${set.setNumber}"
        
        // Remove listeners before setting text to avoid infinite loops/wrong updates
        holder.weight.onFocusChangeListener = null
        holder.reps.onFocusChangeListener = null
        
        holder.weight.setText(if (set.weightUsed == 0f) "" else set.weightUsed.toString())
        holder.reps.setText(if (set.reps == 0) "" else set.reps.toString())
        
        holder.completeCheckbox.isChecked = set.isCompleted
        
        updateSetRowStyle(holder, set.isCompleted)

        val saveChanges = {
            val newWeight = holder.weight.text.toString().toFloatOrNull() ?: 0f
            val newReps = holder.reps.text.toString().toIntOrNull() ?: 0
            if (newWeight != set.weightUsed || newReps != set.reps) {
                onSetUpdate(set, newReps, newWeight)
            }
        }

        holder.weight.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveChanges()
        }
        
        holder.reps.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) saveChanges()
        }

        holder.deleteButton.setOnClickListener { onSetDeleteClick(set) }
        
        holder.completeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != set.isCompleted) {
                onSetCompleteChange(set, isChecked)
            }
        }
    }

    private fun updateSetRowStyle(holder: ViewHolder, isCompleted: Boolean) {
        val alpha = if (isCompleted) 0.5f else 1.0f
        holder.itemView.alpha = alpha
        
        if (isCompleted) {
            holder.reps.paintFlags = holder.reps.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            holder.weight.paintFlags = holder.weight.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.reps.paintFlags = holder.reps.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.weight.paintFlags = holder.weight.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun getItemCount(): Int {
        return sets.size
    }

    fun updateData(newSets: List<Set>) {
        sets = newSets
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val setNumber: TextView = itemView.findViewById(R.id.text_view_set_number)
        val reps: EditText = itemView.findViewById(R.id.edit_text_reps)
        val weight: EditText = itemView.findViewById(R.id.edit_text_weight)
        val deleteButton: View = itemView.findViewById(R.id.button_delete_set)
        val completeCheckbox: com.google.android.material.checkbox.MaterialCheckBox = itemView.findViewById(R.id.checkbox_complete)
    }
}

package com.example.gymworkout.ui.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Set

class SetAdapter(
    initialSets: List<Set>,
    private val onSetUpdate: (Set, Int, Float) -> Unit,
    private val onSetDeleteClick: (Set) -> Unit,
    private val onSetCompleteChange: (Set, Boolean) -> Unit
) : RecyclerView.Adapter<SetAdapter.ViewHolder>() {

    var sets: MutableList<Set> = initialSets.toMutableList()

    private var focusOnPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val set = sets[position]
        holder.setNumber.text = "${set.setNumber}"
        
        // Remove listeners before setting text to avoid infinite loops/wrong updates
        holder.weight.removeTextChangedListener(holder.weightWatcher)
        holder.reps.removeTextChangedListener(holder.repsWatcher)
        
        val weightStr = if (set.weightUsed == 0f) "" else {
            if (set.weightUsed == set.weightUsed.toLong().toFloat()) {
                set.weightUsed.toLong().toString()
            } else {
                set.weightUsed.toString()
            }
        }
        val repsStr = if (set.reps == 0) "" else set.reps.toString()

        if (holder.weight.text.toString() != weightStr) {
            holder.weight.setText(weightStr)
        }
        if (holder.reps.text.toString() != repsStr) {
            holder.reps.setText(repsStr)
        }
        
        holder.completeCheckbox.isChecked = set.isCompleted
        
        updateSetRowStyle(holder, set.isCompleted)

        // Add Watchers
        holder.weight.addTextChangedListener(holder.weightWatcher)
        holder.reps.addTextChangedListener(holder.repsWatcher)

        if (position == focusOnPosition) {
            holder.weight.requestFocus()
            focusOnPosition = null
        }

        holder.deleteButton.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onSetDeleteClick(sets[pos])
            }
        }
        
        holder.completeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                val currentItem = sets[pos]
                if (isChecked != currentItem.isCompleted) {
                    onSetCompleteChange(currentItem, isChecked)
                }
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
        val oldSize = sets.size
        val newSize = newSets.size
        sets = newSets.toMutableList()
        
        if (newSize == oldSize + 1) {
            focusOnPosition = newSize - 1
            notifyItemInserted(newSize - 1)
        } else if (newSize == oldSize - 1) {
            notifyDataSetChanged()
        } else {
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val setNumber: TextView = itemView.findViewById(R.id.text_view_set_number)
        val reps: EditText = itemView.findViewById(R.id.edit_text_reps)
        val weight: EditText = itemView.findViewById(R.id.edit_text_weight)
        val deleteButton: View = itemView.findViewById(R.id.button_delete_set)
        val completeCheckbox: com.google.android.material.checkbox.MaterialCheckBox = itemView.findViewById(R.id.checkbox_complete)
        
        private fun saveData() {
            val pos = bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return
            
            val currentWeightStr = weight.text.toString()
            val currentRepsStr = reps.text.toString()
            
            val currentWeight = currentWeightStr.toFloatOrNull() ?: 0f
            val currentReps = currentRepsStr.toIntOrNull() ?: 0
            
            val item = sets[pos]
            if (item.weightUsed != currentWeight || item.reps != currentReps) {
                val updatedItem = item.copy(weightUsed = currentWeight, reps = currentReps)
                sets[pos] = updatedItem
                onSetUpdate(updatedItem, currentReps, currentWeight)
            }
        }
        
        val weightWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (weight.hasFocus()) {
                    saveData()
                }
            }
        }

        val repsWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (reps.hasFocus()) {
                    saveData()
                }
            }
        }
    }
}

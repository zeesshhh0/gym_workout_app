package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Set

class SetAdapter(
    var sets: List<Set>,
    private val onSetClick: (Set) -> Unit,
    private val onSetDeleteClick: (Set) -> Unit
) : RecyclerView.Adapter<SetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val set = sets[position]
        holder.setNumber.text = "Set ${set.setNumber}"
        holder.reps.text = "${set.reps} reps"
        holder.weight.text = "${set.weightUsed} kg"
        holder.itemView.setOnClickListener { onSetClick(set) }
        holder.deleteButton.setOnClickListener { onSetDeleteClick(set) }
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
        val reps: TextView = itemView.findViewById(R.id.text_view_reps)
        val weight: TextView = itemView.findViewById(R.id.text_view_weight)
        val deleteButton: View = itemView.findViewById(R.id.button_delete_set)
    }
}
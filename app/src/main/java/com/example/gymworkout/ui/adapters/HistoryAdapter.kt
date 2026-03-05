package com.example.gymworkout.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.HistoryItem
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private var items: List<HistoryItem>,
    private val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MONTH = 0
        private const val VIEW_TYPE_WORKOUT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryItem.MonthHeader -> VIEW_TYPE_MONTH
            is HistoryItem.WorkoutCard -> VIEW_TYPE_WORKOUT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MONTH -> {
                val view = inflater.inflate(R.layout.item_history_month_header, parent, false)
                MonthViewHolder(view)
            }
            VIEW_TYPE_WORKOUT -> {
                val view = inflater.inflate(R.layout.item_history_workout_card, parent, false)
                WorkoutViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is MonthViewHolder -> holder.bind(item as HistoryItem.MonthHeader)
            is WorkoutViewHolder -> holder.bind(item as HistoryItem.WorkoutCard, clickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<HistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val monthName: TextView = itemView.findViewById(R.id.monthNameTextView)
        private val workoutCount: TextView = itemView.findViewById(R.id.workoutCountTextView)

        fun bind(item: HistoryItem.MonthHeader) {
            monthName.text = item.monthName
            workoutCount.text = "${item.workoutCount} ${if (item.workoutCount == 1) "workout" else "workouts"}"
        }
    }

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val workoutName: TextView = itemView.findViewById(R.id.workoutNameTextView)
        private val workoutDate: TextView = itemView.findViewById(R.id.workoutDateTextView)
        private val duration: TextView = itemView.findViewById(R.id.durationTextView)
        private val volume: TextView = itemView.findViewById(R.id.volumeTextView)
        private val exerciseListContainer: LinearLayout = itemView.findViewById(R.id.exercise_list_container)

        fun bind(item: HistoryItem.WorkoutCard, clickListener: (Int) -> Unit) {
            workoutName.text = item.workoutName
            
            // Format date: "Monday, Sep 20 at 08:00"
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            val inputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            val formattedDate = try {
                val date = inputDateFormat.parse(item.date)
                val baseDate = outputDateFormat.format(date ?: java.util.Date())
                val time = item.startTime?.let { 
                    val timeDate = inputTimeFormat.parse(it)
                    outputTimeFormat.format(timeDate ?: java.util.Date())
                } ?: ""
                "$baseDate at $time"
            } catch (e: Exception) {
                item.date
            }
            workoutDate.text = formattedDate

            duration.text = item.stats.getDurationText()
            volume.text = "${item.stats.totalVolume.toInt()} kg"

            // Dynamic exercise list
            exerciseListContainer.removeAllViews()
            val inflater = LayoutInflater.from(itemView.context)
            item.exercises.forEach { exercise ->
                val exerciseView = inflater.inflate(R.layout.item_history_exercise_row, exerciseListContainer, false)
                val exerciseNameText: TextView = exerciseView.findViewById(R.id.exerciseNameTextView)
                val bestSetText: TextView = exerciseView.findViewById(R.id.bestSetTextView)
                
                exerciseNameText.text = "${exercise.setCount} x ${exercise.exerciseName}"
                bestSetText.text = "${exercise.bestSetWeight.toInt()} kg x ${exercise.bestSetReps}"
                
                exerciseListContainer.addView(exerciseView)
            }

            itemView.setOnClickListener { clickListener(item.sessionId) }
        }
    }
}

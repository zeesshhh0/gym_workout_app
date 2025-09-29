package com.example.gymworkout.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.ui.adapters.ExerciseRecyclerAdapter

class ExercisesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseRecyclerAdapter
    private lateinit var exercises: List<Exercise>
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises, container, false)

        dbHelper = DatabaseHelper(requireContext())
        exercises = dbHelper.getAllExercises()

        recyclerView = view.findViewById(R.id.exercisesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ExerciseRecyclerAdapter(exercises) { }
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        exercises = dbHelper.getAllExercises()
        adapter.updateData(exercises)
    }
}
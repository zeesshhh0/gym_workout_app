package com.example.gymworkout.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.ui.adapters.ExerciseRecyclerAdapter
import com.example.gymworkout.ui.exercise.AddExerciseActivity
import com.example.gymworkout.ui.exercise.ExerciseInstructionsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExercisesFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseRecyclerAdapter
    private lateinit var allExercises: MutableList<Exercise>
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var fabAddExercise: FloatingActionButton

    private val addExerciseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshExercises()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercises, container, false)

        dbHelper = DatabaseHelper(requireContext())
        allExercises = dbHelper.getAllExercises().toMutableList()

        recyclerView = view.findViewById(R.id.exercisesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ExerciseRecyclerAdapter(allExercises, { exercise ->
            val intent = Intent(requireContext(), ExerciseInstructionsActivity::class.java)
            intent.putExtra("exercise_name", exercise.name)
            startActivity(intent)
        })
        recyclerView.adapter = adapter

        fabAddExercise = view.findViewById(R.id.fab_add_exercise)
        fabAddExercise.setOnClickListener {
            val intent = Intent(requireContext(), AddExerciseActivity::class.java)
            addExerciseLauncher.launch(intent)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_exercises, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val filteredList = allExercises.filter { exercise ->
            exercise.name.lowercase().contains(newText.orEmpty().lowercase())
        }
        adapter.updateData(filteredList)
        return true
    }

    private fun refreshExercises() {
        allExercises.clear()
        allExercises.addAll(dbHelper.getAllExercises())
        adapter.updateData(allExercises)
    }

    override fun onResume() {
        super.onResume()
        refreshExercises()
        (activity as AppCompatActivity).supportActionBar?.title = "Exercises"
    }
}
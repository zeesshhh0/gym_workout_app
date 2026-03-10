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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.adapters.ExerciseRecyclerAdapter
import com.example.gymworkout.ui.exercise.AddExerciseActivity
import com.example.gymworkout.ui.exercise.ExerciseInstructionsActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ExercisesFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseRecyclerAdapter
    private lateinit var allExercises: List<Exercise>
    private lateinit var repository: WorkoutRepository
    private lateinit var fabAddExercise: FloatingActionButton
    private lateinit var chipGroup: ChipGroup

    private val searchQuery = MutableStateFlow("")
    private val selectedMuscleGroup = MutableStateFlow("All")

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

        repository = WorkoutRepository(requireContext())
        allExercises = repository.getAllExercises()

        recyclerView = view.findViewById(R.id.exercisesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ExerciseRecyclerAdapter({ exercise ->
            val intent = Intent(requireContext(), ExerciseInstructionsActivity::class.java)
            intent.putExtra("exercise_name", exercise.name)
            startActivity(intent)
        })
        recyclerView.adapter = adapter
        adapter.submitList(allExercises)

        chipGroup = view.findViewById(R.id.muscleGroupChipGroup)
        setupFilterChips()

        fabAddExercise = view.findViewById(R.id.fab_add_exercise)
        fabAddExercise.setOnClickListener {
            val intent = Intent(requireContext(), AddExerciseActivity::class.java)
            addExerciseLauncher.launch(intent)
        }

        observeFilters()

        return view
    }

    private fun setupFilterChips() {
        val muscleGroups = repository.getAllMuscleGroups()
        
        // Remove existing chips except "All"
        val allChip = chipGroup.findViewById<Chip>(R.id.chip_all)
        chipGroup.removeAllViews()
        chipGroup.addView(allChip)

        muscleGroups.forEach { mg ->
            // Create chip programmatically with Material 3 style
            val newChip = Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle)
            newChip.text = mg.name
            newChip.isCheckable = true
            chipGroup.addView(newChip)
        }

        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                selectedMuscleGroup.value = "All"
                group.check(R.id.chip_all)
            } else {
                val checkedChipId = checkedIds.firstOrNull()
                if (checkedChipId != null) {
                    val checkedChip = group.findViewById<Chip>(checkedChipId)
                    selectedMuscleGroup.value = checkedChip.text.toString()
                }
            }
        }
    }

    private fun observeFilters() {
        lifecycleScope.launch {
            combine(searchQuery, selectedMuscleGroup) { query, group ->
                Pair(query, group)
            }.collect { (query, group) ->
                applyFilters(query, group)
            }
        }
    }

    private fun applyFilters(query: String, group: String) {
        val filteredList = allExercises.filter { exercise ->
            val matchesSearch = exercise.name.contains(query, ignoreCase = true)
            val matchesFilter = group == "All" || exercise.muscleGroup.equals(group, ignoreCase = true)
            matchesSearch && matchesFilter
        }
        adapter.submitList(filteredList)
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
        searchQuery.value = newText.orEmpty()
        return true
    }

    private fun refreshExercises() {
        allExercises = repository.getAllExercises()
        applyFilters(searchQuery.value, selectedMuscleGroup.value)
    }

    override fun onResume() {
        super.onResume()
        refreshExercises()
        (activity as AppCompatActivity).supportActionBar?.title = "Exercises"
    }
}

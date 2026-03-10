package com.example.gymworkout.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.adapters.ExerciseRecyclerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ExercisesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseRecyclerAdapter
    private lateinit var exercises: List<Exercise>
    private lateinit var repository: WorkoutRepository
    private lateinit var chipGroup: ChipGroup
    private var workoutId: Long = -1

    private val searchQuery = MutableStateFlow("")
    private val selectedMuscleGroup = MutableStateFlow("All")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        workoutId = intent.getLongExtra("workoutId", -1)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = WorkoutRepository(this)
        exercises = repository.getAllExercises()

        recyclerView = findViewById(R.id.exercisesListView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExerciseRecyclerAdapter(
            onItemClick = { exercise ->
                if (workoutId != -1L) {
                    val currentExercises = repository.getExercisesForWorkout(workoutId.toInt())
                    val alreadyExists = currentExercises.any { it.id == exercise.id }
                    
                    if (alreadyExists) {
                        Toast.makeText(this, "${exercise.name} is already in this workout", Toast.LENGTH_SHORT).show()
                    } else {
                        repository.addExerciseToWorkout(workoutId.toInt(), exercise.id)
                        finish()
                    }
                }
            }, showAddSetButton = false
        )
        recyclerView.adapter = adapter
        adapter.submitList(exercises)

        chipGroup = findViewById(R.id.muscleGroupChipGroup)
        setupFilterChips()

        val addExerciseFab = findViewById<FloatingActionButton>(R.id.addExerciseFab)
        addExerciseFab.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }

        observeFilters()
    }

    private fun setupFilterChips() {
        val muscleGroups = repository.getAllMuscleGroups()
        
        // Remove existing chips except "All"
        val allChip = chipGroup.findViewById<Chip>(R.id.chip_all)
        chipGroup.removeAllViews()
        chipGroup.addView(allChip)

        muscleGroups.forEach { mg ->
            val newChip = Chip(this, null, com.google.android.material.R.attr.chipStyle)
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
        val filteredList = exercises.filter { exercise ->
            val matchesSearch = exercise.name.contains(query, ignoreCase = true)
            val matchesFilter = group == "All" || exercise.muscleGroup.equals(group, ignoreCase = true)
            matchesSearch && matchesFilter
        }
        adapter.submitList(filteredList)
    }

    override fun onResume() {
        super.onResume()
        exercises = repository.getAllExercises()
        applyFilters(searchQuery.value, selectedMuscleGroup.value)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_exercises, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery.value = newText.orEmpty()
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

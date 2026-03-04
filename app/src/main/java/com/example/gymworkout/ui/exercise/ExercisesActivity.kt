package com.example.gymworkout.ui.exercise

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.ui.adapters.ExerciseRecyclerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExercisesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseRecyclerAdapter
    private lateinit var exercises: List<Exercise>
    private lateinit var repository: WorkoutRepository
    private var workoutId: Long = -1

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
            exercises = exercises,
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

        val addExerciseFab = findViewById<FloatingActionButton>(R.id.addExerciseFab)
        addExerciseFab.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        exercises = repository.getAllExercises()
        adapter.updateData(exercises)
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
                val filteredExercises = exercises.filter {
                    it.name.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateData(filteredExercises)
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
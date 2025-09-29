package com.example.gymworkout.ui.exercise

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.data.model.Exercise
import com.example.gymworkout.ui.adapters.ExerciseRecyclerAdapter
import com.example.gymworkout.ui.login.LoginActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExercisesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseRecyclerAdapter
    private lateinit var exercises: List<Exercise>
    private var workoutId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        workoutId = intent.getLongExtra("workoutId", -1)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val dbHelper = DatabaseHelper(this)
        exercises = dbHelper.getAllExercises()

        recyclerView = findViewById(R.id.exercisesListView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExerciseRecyclerAdapter(exercises) { exercise ->
            if (workoutId != -1L) {
                dbHelper.addExerciseToWorkout(workoutId.toInt(), exercise.id)
                finish()
            }
        }
        recyclerView.adapter = adapter

        val addExerciseFab = findViewById<FloatingActionButton>(R.id.addExerciseFab)
        addExerciseFab.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val dbHelper = DatabaseHelper(this)
        exercises = dbHelper.getAllExercises()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("user_id")
                editor.apply()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
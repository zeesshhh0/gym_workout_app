package com.example.gymworkout.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        // Load the default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()
            ).commit()
            supportActionBar?.title = "Home"
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        var title: String = ""

        when (item.itemId) {
            R.id.navigation_home -> {
                selectedFragment = HomeFragment()
                title = "Home"
            }
            R.id.navigation_sessions -> {
                selectedFragment = SessionsFragment()
                title = "Workout Sessions"
            }
            R.id.navigation_exercises -> {
                selectedFragment = ExercisesFragment()
                title = "Exercises"
            }
            R.id.navigation_profile -> {
                selectedFragment = ProfileFragment()
                title = "Profile"
            }
        }

        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit()
            supportActionBar?.title = title
        }

        true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.navigation_sessions
        }
    }
}
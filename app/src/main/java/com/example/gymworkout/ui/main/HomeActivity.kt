package com.example.gymworkout.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.gymworkout.R
import com.example.gymworkout.databinding.ActivityHomeBinding
import com.example.gymworkout.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (!isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return // Important to prevent the rest of onCreate from running
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                title = "Workout Tracker"
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
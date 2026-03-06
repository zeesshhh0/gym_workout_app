package com.example.gymworkout.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.databinding.ActivityHomeBinding
import com.example.gymworkout.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var repository: WorkoutRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = WorkoutRepository(this)
        repository.setSyncListeners(
            onFailure = { message ->
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            },
            onUnauthenticated = {
                redirectToLogin()
            }
        )

        // Enable Firestore offline persistence
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            FirebaseFirestore.getInstance().firestoreSettings = settings
        } catch (e: Exception) {
            // Settings can only be set before any other use of the Firestore instance.
        }

        // Check if user is logged in via Firebase
        if (FirebaseAuth.getInstance().currentUser == null) {
            redirectToLogin()
            return // Important to prevent the rest of onCreate from running
        }
        
        // Clean up any incomplete sessions from previous app runs
        repository.cleanUpOrphanedData()



        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener(navListener)

        // Load the default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                HomeFragment()
            ).commit()
            supportActionBar?.title = "Workout Tracker"
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

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.navigation_sessions
        }
    }
}
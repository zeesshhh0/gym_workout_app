package com.example.gymworkout.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.data.sync.FirestoreSyncManager
import com.example.gymworkout.databinding.ActivitySignupBinding
import com.example.gymworkout.ui.main.HomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var repository: WorkoutRepository
    private lateinit var auth: FirebaseAuth
    private val firestoreSyncManager = FirestoreSyncManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Repository
        repository = WorkoutRepository(this)

        binding.signup.setOnClickListener {
            with(binding) {
                usernameLayout.error = null
                emailLayout.error = null
                passwordLayout.error = null

                val username = username.text.toString().trim()
                val email = email.text.toString().trim()
                val password = password.text.toString()

                // Input validation
                if (username.isEmpty()) {
                    usernameLayout.error = "Username cannot be empty"
                    return@setOnClickListener
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.error = "Invalid email format"
                    return@setOnClickListener
                }
                if (password.length < 6) {
                    passwordLayout.error = "Password must be at least 6 characters"
                    return@setOnClickListener
                }

                // Firebase Auth signup
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@SignupActivity) { task ->
                        if (task.isSuccessful) {
                            // Save user to local DB
                            repository.addUser(username, email)

                            Toast.makeText(this@SignupActivity, "Signup successful!", Toast.LENGTH_SHORT).show()
                            
                            handleSignupSync()
                        } else {
                            // If sign in fails, display a message to the user.
                            val errorMessage = task.exception?.message ?: "Signup failed."
                            Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_LONG).show()
                            emailLayout.error = errorMessage
                        }
                    }
            }
        }

        binding.loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleSignupSync() {
        if (repository.hasLocalData()) {
            showImportLocalDataPrompt()
        } else {
            navigateToHome()
        }
    }

    private fun showImportLocalDataPrompt() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Import Local Data")
            .setMessage("You have local workout data. Would you like to import it into your new cloud account?")
            .setPositiveButton("Import to Cloud") { _, _ ->
                val progressDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Syncing Data")
                    .setMessage("Syncing your workouts...")
                    .setCancelable(false)
                    .show()

                lifecycleScope.launch(Dispatchers.IO) {
                    repository.pushLocalToCloud()
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(this@SignupActivity, "Local data pushed to cloud", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                }
            }
            .setNegativeButton("Keep Local Only") { _, _ ->
                navigateToHome()
            }
            .setCancelable(false)
            .show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
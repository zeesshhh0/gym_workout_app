package com.example.gymworkout.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.data.sync.FirestoreSyncManager
import com.example.gymworkout.databinding.ActivityLoginBinding
import com.example.gymworkout.ui.main.HomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: WorkoutRepository
    private val firestoreSyncManager = FirestoreSyncManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        repository = WorkoutRepository(this)

        // Check if user is already logged in via Firebase
        if (auth.currentUser != null) {
            navigateToHome()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            with(binding) {
                emailLayout.error = null
                passwordLayout.error = null

                val email = email.text.toString().trim()
                val password = password.text.toString()

                // Input validation
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.error = "Invalid email format"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    passwordLayout.error = "Password cannot be empty"
                    return@setOnClickListener
                }

                // Firebase Auth sign-in
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                            handleLoginRestore()
                        } else {
                            val errorMessage = task.exception?.message ?: "Invalid email or password"
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                            passwordLayout.error = errorMessage
                        }
                    }
            }
        }

        binding.signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun handleLoginRestore() {
        val progressDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Syncing Data")
            .setMessage("Checking for cloud data...")
            .setCancelable(false)
            .show()

        firestoreSyncManager.fetchAllUserData(
            onSuccess = { allData ->
                progressDialog.dismiss()
                if (allData.isNotEmpty()) {
                    if (!repository.hasLocalData()) {
                        // Auto-restore
                        repository.restoreUserData(allData)
                        Toast.makeText(this, "Data restored from cloud", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    } else {
                        // Prompt user
                        showRestorePrompt(allData)
                    }
                } else {
                    navigateToHome()
                }
            },
            onFailure = { e ->
                progressDialog.dismiss()
                // Just log and navigate, don't block login
                android.util.Log.e("LoginActivity", "Sync check failed", e)
                navigateToHome()
            }
        )
    }

    private fun showRestorePrompt(allData: List<FirestoreSyncManager.WorkoutData>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cloud Data Found")
            .setMessage("You have workout data in the cloud. Would you like to restore it and overwrite local data, or keep your current local data?")
            .setPositiveButton("Restore from Cloud") { _, _ ->
                repository.restoreUserData(allData)
                Toast.makeText(this, "Data restored from cloud", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            .setNegativeButton("Keep Local Data") { _, _ ->
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
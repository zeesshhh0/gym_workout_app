package com.example.gymworkout.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.data.sync.FirestoreSyncManager
import com.example.gymworkout.databinding.ActivityLoginBinding
import com.example.gymworkout.ui.main.HomeActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            .setMessage("Syncing your workouts...")
            .setCancelable(false)
            .show()

        // First, fetch and save the user profile locally
        firestoreSyncManager.fetchUserProfile { username, email ->
            repository.addUser(username, email)
        }

        firestoreSyncManager.fetchAllUserData(
            onSuccess = { allData ->
                if (allData.isEmpty()) {
                    // Condition A: Cloud is empty.
                    if (repository.hasLocalData()) {
                        progressDialog.dismiss()
                        showImportLocalDataPrompt()
                    } else {
                        progressDialog.dismiss()
                        navigateToHome()
                    }
                } else {
                    // Condition B: Cloud has data. Show Sync Conflict Dialog.
                    progressDialog.dismiss()
                    showSyncConflictDialog(allData)
                }
            },
            onFailure = { e ->
                progressDialog.dismiss()
                android.util.Log.e("LoginActivity", "Sync check failed", e)
                navigateToHome()
            }
        )
    }

    private fun showImportLocalDataPrompt() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Import Local Data")
            .setMessage("You have local workout data. Would you like to import it into your cloud account?")
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
                        Toast.makeText(this@LoginActivity, "Local data pushed to cloud", Toast.LENGTH_SHORT).show()
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

    private fun showSyncConflictDialog(allData: List<FirestoreSyncManager.WorkoutData>) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Existing Data Found")
            .setMessage("Your account already has saved workouts. How would you like to sync your data?")
            .setPositiveButton("Use Cloud Data") { _, _ ->
                val progressDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Syncing Data")
                    .setMessage("Syncing your workouts...")
                    .setCancelable(false)
                    .show()

                lifecycleScope.launch(Dispatchers.IO) {
                    repository.restoreUserData(allData)
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(this@LoginActivity, "Data restored from cloud", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                }
            }
            .setNegativeButton("Use Local Data") { _, _ ->
                val progressDialog = MaterialAlertDialogBuilder(this)
                    .setTitle("Syncing Data")
                    .setMessage("Syncing your workouts...")
                    .setCancelable(false)
                    .show()

                lifecycleScope.launch(Dispatchers.IO) {
                    repository.pushLocalToCloud()
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(this@LoginActivity, "Local data pushed to cloud", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                }
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
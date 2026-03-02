package com.example.gymworkout.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.databinding.ActivitySignupBinding
import com.example.gymworkout.ui.main.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var repository: WorkoutRepository
    private lateinit var auth: FirebaseAuth

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

                            // Navigate to HomeActivity
                            val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
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
}
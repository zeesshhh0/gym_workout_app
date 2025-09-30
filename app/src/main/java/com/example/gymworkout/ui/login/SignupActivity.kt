package com.example.gymworkout.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.databinding.ActivitySignupBinding
import com.example.gymworkout.ui.main.HomeActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(this)

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

                val userId = dbHelper.addUser(username, email, password)
                if (userId.toInt() != -1) {
                    // Save user session
                    val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putInt("user_id", userId.toInt())
                    editor.putBoolean("is_logged_in", true)
                    editor.apply()

                    Toast.makeText(this@SignupActivity, "Signup successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to HomeActivity
                    val intent = Intent(this@SignupActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    emailLayout.error = "Signup failed. Email might already be in use."
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
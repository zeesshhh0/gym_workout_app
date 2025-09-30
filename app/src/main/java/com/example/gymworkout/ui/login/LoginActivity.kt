package com.example.gymworkout.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.databinding.ActivityLoginBinding
import com.example.gymworkout.ui.main.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("user_id")) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

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

                val userId = dbHelper.checkUser(email, password)
                if (userId != -1) {
                    // Save user session
                    val editor = sharedPreferences.edit()
                    editor.putInt("user_id", userId)
                    editor.putBoolean("is_logged_in", true)
                    editor.apply()

                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to HomeActivity
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    passwordLayout.error = "Invalid email or password"
                }
            }
        }

        binding.signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}
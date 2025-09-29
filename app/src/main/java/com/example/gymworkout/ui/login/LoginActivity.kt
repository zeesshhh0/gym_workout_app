package com.example.gymworkout.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.ui.exercise.ExercisesActivity
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        if (sharedPreferences.contains("user_id")) {
            startActivity(Intent(this, ExercisesActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        dbHelper = DatabaseHelper(this)

        binding.login.setOnClickListener {
            val email = binding.email?.text.toString()
            val password = binding.password1?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = dbHelper.checkUser(email, password)
            if (userId != -1) {
                // Login successful
                val editor = sharedPreferences.edit()
                editor.putInt("user_id", userId)
                editor.apply()

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ExercisesActivity::class.java))
                finish()
            } else {
                // Login failed
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupLink?.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}

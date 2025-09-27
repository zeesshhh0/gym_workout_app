package com.example.gymworkout.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkout.MainActivity
import com.example.gymworkout.data.DatabaseHelper
import com.example.gymworkout.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.login.setOnClickListener {
            val email = binding.email?.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.checkUser(email, password)) {
                // Login successful
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
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

package com.example.gymworkout.ui.profile

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText

class EditProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)

        val usernameEditText = findViewById<TextInputEditText>(R.id.editTextUsername)
        val emailEditText = findViewById<TextInputEditText>(R.id.editTextEmail)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        if (userId != -1) {
            val userDetails = dbHelper.getUserDetails(userId)
            userDetails?.let {
                usernameEditText.setText(it.first)
                emailEditText.setText(it.second)
            }
        }

        saveButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val newEmail = emailEditText.text.toString().trim()

            if (newUsername.isNotEmpty() && newEmail.isNotEmpty()) {
                dbHelper.updateUserDetails(userId, newUsername, newEmail)
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Username and email cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

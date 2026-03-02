package com.example.gymworkout.ui.profile

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.gymworkout.R
import com.example.gymworkout.data.repository.WorkoutRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class EditProfileActivity : AppCompatActivity() {

    private lateinit var repository: WorkoutRepository
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        repository = WorkoutRepository(this)
        userId = FirebaseAuth.getInstance().currentUser?.uid

        val usernameEditText = findViewById<TextInputEditText>(R.id.editTextUsername)
        val emailEditText = findViewById<TextInputEditText>(R.id.editTextEmail)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val userDetails = repository.getUserDetails()
        userDetails?.let {
            usernameEditText.setText(it.first)
            emailEditText.setText(it.second)
        }

        saveButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val newEmail = emailEditText.text.toString().trim()

            if (newUsername.isNotEmpty() && newEmail.isNotEmpty()) {
                repository.updateUserDetails(newUsername, newEmail)
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

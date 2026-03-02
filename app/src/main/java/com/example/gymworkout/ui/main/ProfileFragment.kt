package com.example.gymworkout.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.example.gymworkout.data.db.DatabaseHelper
import com.example.gymworkout.ui.login.LoginActivity
import com.example.gymworkout.ui.profile.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private var userId: String? = null
    private lateinit var auth: FirebaseAuth

    private val editProfileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadUserData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        auth = FirebaseAuth.getInstance()

        usernameTextView = view.findViewById(R.id.textViewUsername)
        emailTextView = view.findViewById(R.id.textViewEmail)
        val editProfileButton = view.findViewById<Button>(R.id.buttonEditProfile)
        val changePasswordButton = view.findViewById<Button>(R.id.buttonChangePassword)
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)

        userId = auth.currentUser?.uid

        loadUserData()

        logoutButton.setOnClickListener {
            // Firebase Sign Out
            auth.signOut()

            // Clear SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // Navigate to LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }

        editProfileButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            editProfileLauncher.launch(intent)
        }

        changePasswordButton.setOnClickListener {
            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        userId?.let { uid ->
            val userDetails = dbHelper.getUserDetails(uid)
            userDetails?.let {
                usernameTextView.text = "Username: ${it.first}"
                emailTextView.text = "Email: ${it.second}"
            }
        }
    }
}
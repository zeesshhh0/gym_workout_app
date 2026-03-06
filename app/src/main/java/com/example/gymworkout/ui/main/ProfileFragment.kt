package com.example.gymworkout.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.example.gymworkout.data.repository.WorkoutRepository
import com.example.gymworkout.data.sync.FirestoreSyncManager
import com.example.gymworkout.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var repository: WorkoutRepository
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var auth: FirebaseAuth
    private val firestoreSyncManager = FirestoreSyncManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = WorkoutRepository(requireContext())
        auth = FirebaseAuth.getInstance()

        usernameTextView = view.findViewById(R.id.textViewUsername)
        emailTextView = view.findViewById(R.id.textViewEmail)
        val logoutButton = view.findViewById<Button>(R.id.buttonLogout)
        val restoreCloudButton = view.findViewById<Button>(R.id.buttonRestoreCloud)

        if (auth.currentUser == null) {
            usernameTextView.text = "Not Logged In"
            emailTextView.text = "Local Account"
            restoreCloudButton.visibility = View.GONE
            logoutButton.text = "Log In / Sign Up"
            logoutButton.setOnClickListener {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            loadUserData()

            restoreCloudButton.setOnClickListener {
                showRestoreConfirmationDialog()
            }

            logoutButton.setOnClickListener {
                auth.signOut()
                
                val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun loadUserData() {
        val userDetails = repository.getUserDetails()
        userDetails?.let {
            usernameTextView.text = "Username: ${it.first}"
            emailTextView.text = "Email: ${it.second}"
        }
    }

    private fun showRestoreConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Restore from Cloud")
            .setMessage("Cloud data found. This will overwrite local data. Continue?")
            .setPositiveButton("Continue") { _, _ ->
                restoreFromCloud()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun restoreFromCloud() {
        val progressDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Restoring Data")
            .setMessage("Please wait while data is being restored from the cloud...")
            .setCancelable(false)
            .show()
        
        firestoreSyncManager.fetchAllUserData(
            onSuccess = { allData ->
                progressDialog.dismiss()
                repository.restoreUserData(allData)
                Toast.makeText(context, "Data restored successfully", Toast.LENGTH_SHORT).show()
                loadUserData()
            },
            onFailure = { e ->
                progressDialog.dismiss()
                Toast.makeText(context, "Failed to restore data: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }
}

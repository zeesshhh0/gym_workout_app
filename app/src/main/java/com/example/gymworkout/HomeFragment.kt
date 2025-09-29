package com.example.gymworkout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val buttonCreateWorkout: Button = view.findViewById(R.id.buttonCreateWorkout)
        buttonCreateWorkout.setOnClickListener {
            val intent = Intent(activity, WorkoutActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
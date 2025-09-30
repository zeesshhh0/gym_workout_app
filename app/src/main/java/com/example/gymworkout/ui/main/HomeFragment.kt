package com.example.gymworkout.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.gymworkout.R
import com.example.gymworkout.ui.workout.WorkoutActivity

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
            activity?.startActivityForResult(intent, 1)
        }

        return view
    }
}
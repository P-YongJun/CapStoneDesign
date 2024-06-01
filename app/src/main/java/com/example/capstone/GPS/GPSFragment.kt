package com.example.capstone.GPS

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.capstone.R

class GPSFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gps, container, false)
    }

    companion object {
        fun newInstance(): GPSFragment {
            return GPSFragment()
        }
    }
}
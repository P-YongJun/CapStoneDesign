package com.example.capstone.Heart_Rate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.capstone.R

class HeartRateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_heart_rate, container, false)
    }

    companion object {
        fun newInstance(): HeartRateFragment {
            return HeartRateFragment()        }
    }
}
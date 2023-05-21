package com.example.taskhelper.ui.whether

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskhelper.R
import com.example.taskhelper.databinding.FragmentWeatherBinding


class WeatherFragment : Fragment() {
  lateinit var binding:FragmentWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(inflater,container,false)
        return binding.root
    }


}
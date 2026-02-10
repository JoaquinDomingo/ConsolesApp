package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.consolas.R
import com.example.consolas.databinding.FragmentJoaquinBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentJoaquin : Fragment(R.layout.fragment_joaquin) {

    private lateinit var binding: FragmentJoaquinBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJoaquinBinding.bind(view)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_profile)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_settings)
        }
    }
}
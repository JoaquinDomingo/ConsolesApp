package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.consolas.R
import com.example.consolas.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        setupListeners()
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val mensaje = if (isChecked) "Modo oscuro activado" else "Modo oscuro desactivado"
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
        }

        binding.checkNotifications.setOnCheckedChangeListener { _, isChecked ->
            val mensaje = if (isChecked) "Notificaciones activas" else "Notificaciones silenciadas"
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
        }

        binding.btnChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), "Función para cambiar contraseña", Toast.LENGTH_SHORT).show()
        }

        binding.btnHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo soporte técnico", Toast.LENGTH_SHORT).show()
        }
    }
}
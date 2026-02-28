package com.example.consolas.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.consolas.R
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var sessionManager: SessionManager

    // Launcher para pedir permiso de notificaciones (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sessionManager.setNotificationsEnabled(true)
            Toast.makeText(requireContext(), "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        } else {
            binding.checkNotifications.isChecked = false
            sessionManager.setNotificationsEnabled(false)
            Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        // Cargamos los estados guardados en el SessionManager
        binding.checkNotifications.isChecked = sessionManager.areNotificationsEnabled()

        // Para el modo oscuro, comprobamos la configuración actual del sistema/app
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.switchDarkMode.isChecked = isDarkMode
    }

    private fun setupListeners() {
        // Listener Modo Oscuro
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Listener Notificaciones con Pop-up
        binding.checkNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkNotificationPermission()
            } else {
                sessionManager.setNotificationsEnabled(false)
                Toast.makeText(requireContext(), "Notificaciones silenciadas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnChangePassword.setOnClickListener {
            // Aquí iría tu navegación a un fragmento de cambio de password
            Toast.makeText(requireContext(), "Función de seguridad próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Contacto: soporte@tuapp.com", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    sessionManager.setNotificationsEnabled(true)
                }
                else -> {
                    // Pedir permiso explícito al usuario
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // En versiones anteriores a Android 13, el permiso se da al instalar
            sessionManager.setNotificationsEnabled(true)
        }
    }
}
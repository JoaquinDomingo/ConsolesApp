package com.example.consolas.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.ActivityLoginBinding
import com.example.consolas.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Verificación rápida: Si ya hay token, saltamos el login
        if (sessionManager.isUserLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Observamos el estado de carga para mostrar un feedback visual
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.btnLogin.isEnabled = !isLoading
            binding.btnRegister.isEnabled = !isLoading
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        authViewModel.authState.observe(this) { result ->
            result?.onSuccess { isLogin ->
                if (isLogin) {
                    // LOGIN ÉXITO: El token ya está guardado en SessionManager (vía Repo)
                    Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    // REGISTRO ÉXITO: El servidor respondió 201 Created
                    Toast.makeText(this, "Registro completado. Ya puedes entrar.", Toast.LENGTH_LONG).show()
                }
            }?.onFailure { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToMain() {
        // Ya no hace falta pasar extras porque el email/token están en SessionManager
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.login(email, pass)
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.register(email, pass)
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
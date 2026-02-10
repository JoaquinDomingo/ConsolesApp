package com.example.consolas.ui.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.consolas.databinding.ActivityLoginBinding
import com.example.consolas.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        authViewModel.authState.observe(this) { result ->
            result.onSuccess {
                val email = binding.etEmail.text.toString()
                val user = email.split("@")[0]
                navigateToMain(user, email)
            }.onFailure { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Llamamos a la lógica de Firebase a través del ViewModel
                authViewModel.login(email, pass)
            } else {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.register(email, pass)
            } else {
                Toast.makeText(this, "Rellena los campos para registrarte", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRecover.setOnClickListener {
            Toast.makeText(this, "Función de recuperación no implementada todavía", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMain(user: String, email: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_NAME", user)
            putExtra("USER_EMAIL", email)
        }
        startActivity(intent)
        finish()
    }
}
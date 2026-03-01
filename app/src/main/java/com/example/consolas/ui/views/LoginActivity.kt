package com.example.consolas.ui.views

import android.content.Intent
import android.os.Bundle
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

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        authViewModel.authState.observe(this) { result ->
            result?.onSuccess { isLogin ->
                if (isLogin) {
                    // Al llegar aquí, el Repository ya guardó el nombre en el SessionManager
                    Toast.makeText(this, "¡Bienvenido, ${sessionManager.userName()}!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
                }
                authViewModel.clearState()
            }?.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val user = binding.etUser.text.toString().trim() // Capturamos el nombre

            if (email.isNotEmpty() && pass.isNotEmpty() && user.isNotEmpty()) {
                authViewModel.login(email, pass, user) // <--- Ya no dará error
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                authViewModel.register(email, pass)
            }
        }
    }
}
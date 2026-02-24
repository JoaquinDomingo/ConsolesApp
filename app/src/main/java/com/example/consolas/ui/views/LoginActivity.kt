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

    @Inject
    lateinit var sessionManager: SessionManager

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

    private fun navigateToMain(user: String, email: String) {
        // PASO CRUCIAL: Guardar en preferencias para que el resto de la App tenga el email
        sessionManager.setUser(email, user)

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_NAME", user)
            putExtra("USER_EMAIL", email)
        }
        startActivity(intent)
        finish()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) authViewModel.login(email, pass)
        }
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty()) authViewModel.register(email, pass)
        }
    }
}
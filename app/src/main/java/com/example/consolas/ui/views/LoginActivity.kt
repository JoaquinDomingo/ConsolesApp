package com.example.consolas.ui.views

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.consolas.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString()
            val pass = binding.etPassword.text.toString()
            val email = binding.etEmail.text.toString()

            if (user == "admin" && pass == "1234") {
                navigateToMain(user, email)
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            // Lógica de registro
        }

        binding.btnRecover.setOnClickListener {
            // Lógica de recuperación
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
package com.example.consolas.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.consolas.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val user = binding.etUser.text.toString()
            val pass = binding.etPassword.text.toString()
            val email = binding.etEmail.text.toString() // Capturamos el email del usuario

            if (user == "admin" && pass == "1234") {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_NAME", user)
                    putExtra("USER_EMAIL", email) // Pasamos el email mediante el intent
                }
                startActivity(intent)
                finish()
            } else {
                // Opcional: aviso de error
            }
        }


        binding.btnRegister.setOnClickListener {
            // Más adelante
        }

        binding.btnRecover.setOnClickListener {
            // Más adelante
        }
    }
}

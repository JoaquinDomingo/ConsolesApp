package com.example.consolas.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.consolas.R
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.FragmentProfileBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import com.example.consolas.ui.views.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    // Usamos activityViewModels para compartir los datos con el resto de la app
    private val viewModel: ConsoleViewModel by activityViewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        setupUserUI()
        observeStatistics()
        setupButtons()
    }

    private fun setupUserUI() {
        // Mostramos los datos del usuario actual
        val email = sessionManager.userEmail()
        binding.tvProfileEmail.text = email

        // Si el nombre viene del email (antes de la @), lo extraemos para el título
        val name = email.substringBefore("@").uppercase()
        binding.tvProfileName.text = name
    }

    private fun observeStatistics() {
        // Observamos el LiveData de consolas para actualizar contadores en tiempo real
        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            if (lista != null) {
                val total = lista.size
                val favoritos = lista.count { it.favorite }
                val precioTotal = lista.sumOf { it.price }
                val precioMedio = if (total > 0) precioTotal / total else 0.0

                // Actualizamos los TextViews del XML
                binding.tvTotalConsoles.text = "Total consolas: $total"
                binding.tvFavCount.text = "Favoritos: $favoritos"
                binding.tvAvgPrice.text = "Precio medio: ${String.format("%.2f", precioMedio)} €"
            }
        }
    }

    private fun setupButtons() {
        binding.btnLogout.setOnClickListener {
            // 1. Limpiamos los SharedPreferences
            sessionManager.clear()

            // 2. Creamos el Intent para ir a LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)

            // 3. Limpiamos el historial de pantallas para que no pueda volver atrás
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            requireActivity().finish() // Cerramos la Activity actual (MainActivity)
        }
    }
}
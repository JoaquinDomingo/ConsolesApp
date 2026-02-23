package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.consolas.R
import com.example.consolas.databinding.FragmentProfileBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    //private val viewModel: ConsoleViewModel by viewModels()
    private val vm: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        //setupUserData()
        //setupUserStats()

        binding.tvProfileName.text = vm.userName.ifBlank { "Usuario" }.uppercase()
        binding.tvProfileEmail.text = vm.userEmail.ifBlank { "correo@ejemplo.com" }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.stats.collect { stats ->
                binding.tvFavCount.text = "Favoritos: ${stats.favoritesCount}"
                binding.tvAvgPrice.text = "Precio medio: ${String.format("%.2f", stats.averagePrice)} €"
            }
        }
    }

    \*
    private fun setupUserData() {
        // Recuperamos los datos que enviamos desde el LoginActivity a la MainActivity
        val intent = requireActivity().intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Usuario"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: "correo@ejemplo.com"

        // Los asignamos a los TextViews de tu diseño bonito
        binding.tvProfileName.text = userName.uppercase()
        binding.tvProfileEmail.text = userEmail
    }

    private fun setupUserStats() {
        viewModel.consoles.observe(viewLifecycleOwner) { list ->
            val total = list.size
            // Actualiza la tarjeta de estadísticas que diseñamos
            binding.tvStatsCount.text = "Consolas registradas: $total"
        }
    }*/
}
package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.consolas.R
import com.example.consolas.databinding.FragmentProfileBinding
import com.example.consolas.ui.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupUI()
        observeStats()
    }

    private fun setupUI() {
        // Usamos safe access para evitar crash si el binding es null
        binding.tvProfileName.text = vm.userName.ifBlank { "Usuario" }.uppercase()
        binding.tvProfileEmail.text = vm.userEmail.ifBlank { "Sin sesión activa" }
    }

    private fun observeStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.stats.collect { stats ->
                    // Verificamos que el binding siga existiendo antes de actualizar
                    _binding?.let { b ->
                        b.tvFavCount.text = "Favoritos: ${stats.favoritesCount}"

                        // Formateo seguro para evitar crashes con valores extraños
                        val price = if (stats.averagePrice.isNaN()) 0.0 else stats.averagePrice
                        b.tvAvgPrice.text = String.format(Locale.getDefault(), "Precio medio: %.2f €", price)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // CRUCIAL para evitar que el Flow intente tocar una vista destruida
    }
}
package com.example.consolas.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.FragmentProfileBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import com.example.consolas.ui.views.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ConsoleViewModel by activityViewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        setupUserUI()
        loadSavedProfileImage()
        observeStats()

        // Configurar navegación al editor
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Configurar Logout
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun observeStats() {
        viewModel.consoles.observe(viewLifecycleOwner) { list ->
            val total = list.size
            val favorites = list.count { it.favorite }
            val totalPrice = list.sumOf { it.price }

            binding.tvTotalConsoles.text = "Total consolas: $total"
            binding.tvFavCount.text = "Favoritos: $favorites"
            binding.tvAvgPrice.text = String.format(Locale.getDefault(), "Precio total: %.2f €", totalPrice)
        }
    }

    private fun setupUserUI() {
        val email = sessionManager.userEmail()
        binding.tvProfileEmail.text = email
        binding.tvProfileName.text = sessionManager.userName().ifEmpty {
            email.substringBefore("@")
        }.uppercase()
    }

    private fun loadSavedProfileImage() {
        sessionManager.getProfileImage()?.let { savedUri ->
            Glide.with(this)
                .load(savedUri.toUri())
                .circleCrop()
                .placeholder(R.drawable.images)
                .error(R.drawable.images)
                .into(binding.ivProfilePicture)
        }
    }
}
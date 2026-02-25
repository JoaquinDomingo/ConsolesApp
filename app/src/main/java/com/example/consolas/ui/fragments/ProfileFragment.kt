package com.example.consolas.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.FragmentProfileBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import com.example.consolas.ui.views.LoginActivity
import com.example.consolas.ui.views.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ConsoleViewModel by activityViewModels()

    @Inject lateinit var sessionManager: SessionManager

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { saveAndSyncImage(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        setupUserUI()
        loadSavedProfileImage()

        binding.ivProfilePicture.setOnClickListener {
            pickMedia.launch("image/*")
        }

        binding.btnLogout.setOnClickListener {
            // USAMOS LOGOUT() EN LUGAR DE CLEAR() PARA NO BORRAR LA FOTO
            sessionManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun saveAndSyncImage(uri: Uri) {
        try {
            // SOLICITAR PERMISO PERSISTENTE (Vital para que no se borre al cerrar la app)
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
            requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        sessionManager.saveProfileImage(uri.toString())

        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivProfilePicture)

        (activity as? MainActivity)?.updateNavHeaderImage(uri)
    }

    private fun loadSavedProfileImage() {
        val savedUri = sessionManager.getProfileImage()
        if (savedUri != null) {
            Glide.with(this)
                .load(Uri.parse(savedUri))
                .circleCrop()
                .placeholder(R.drawable.images)
                .error(R.drawable.images)
                .into(binding.ivProfilePicture)
        }
    }

    private fun setupUserUI() {
        val email = sessionManager.userEmail()
        binding.tvProfileEmail.text = email
        binding.tvProfileName.text = sessionManager.userName().ifEmpty {
            email.substringBefore("@").uppercase()
        }
    }
}
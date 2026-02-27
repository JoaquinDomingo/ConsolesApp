package com.example.consolas.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.data.local.SessionManager
import com.example.consolas.databinding.FragmentEditProfileBinding
import com.example.consolas.ui.views.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding
    @Inject lateinit var sessionManager: SessionManager

    private var tempCameraUri: Uri? = null
    private var selectedImageUri: Uri? = null

    // --- Launchers migrados del ProfileFragment ---
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) launchCamera() else Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { handleImageSelection(it, isGallery = true) }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempCameraUri?.let { handleImageSelection(it, isGallery = false) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)

        loadCurrentData()

        // El click en la imagen ahora abre las opciones en el editor
        binding.ivEditProfilePicture.setOnClickListener { showImageOptions() }

        binding.btnSaveProfile.setOnClickListener {
            saveChanges()
        }
    }

    private fun loadCurrentData() {
        val currentEmail = sessionManager.userEmail()
        val currentName = sessionManager.userName()

        binding.etEditName.setText(currentName)
        binding.etEditEmail.setText(currentEmail)

        // Cargar imagen actual
        sessionManager.getProfileImage()?.let {
            selectedImageUri = it.toUri()
            Glide.with(this)
                .load(it)
                .circleCrop()
                .placeholder(R.drawable.images)
                .into(binding.ivEditProfilePicture)
        }
    }

    private fun showImageOptions() {
        val options = arrayOf("Hacer Foto", "Elegir de Galería")
        AlertDialog.Builder(requireContext())
            .setTitle("Cambiar Foto de Perfil")
            .setItems(options) { _, which ->
                if (which == 0) checkCameraPermission() else pickMedia.launch("image/*")
            }.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        try {
            val directory = File(requireContext().cacheDir, "images")
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, "temp_edit_profile.jpg")
            val authority = "${requireActivity().packageName}.provider"
            tempCameraUri = FileProvider.getUriForFile(requireContext(), authority, file)
            takePhoto.launch(tempCameraUri)
        } catch (e: Exception) {
            Log.e("CAMERA_ERROR", "Causa: ${e.message}")
        }
    }

    private fun handleImageSelection(uri: Uri, isGallery: Boolean) {
        if (isGallery) {
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) { e.printStackTrace() }
        }

        selectedImageUri = uri

        // Previsualización inmediata en el editor
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.ivEditProfilePicture)
    }

    private fun saveChanges() {
        // Especificamos tipos String para evitar errores de inferencia
        val name: String = binding.etEditName.text.toString().trim()
        val email: String = binding.etEditEmail.text.toString().trim()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            // 1. Usamos setUser como tienes en tu SessionManager
            sessionManager.setUser(email, name)

            // 2. Guardamos la imagen si existe
            selectedImageUri?.let { uri: Uri ->
                sessionManager.saveProfileImage(uri.toString())
            }

            // 3. Llamamos a la función actualizada de la MainActivity
            (activity as? MainActivity)?.updateNavHeaderData()

            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
            Toast.makeText(requireContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show()
        }
    }
}
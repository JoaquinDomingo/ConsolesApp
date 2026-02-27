package com.example.consolas.ui.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.consolas.R
import com.example.consolas.databinding.FragmentAddConsoleBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

@AndroidEntryPoint
class AddConsoleFragment : Fragment(R.layout.fragment_add_console) {

    private lateinit var binding: FragmentAddConsoleBinding
    private val viewModel: ConsoleViewModel by viewModels()


    private var currentPhotoUri: Uri? = null
    private var finalImageUriStr: String? = null


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            finalImageUriStr = it.toString()
            binding.ivConsolePreview.apply {
                setImageURI(it)
                clearColorFilter()
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let {
                finalImageUriStr = it.toString()
                binding.ivConsolePreview.apply {
                    setImageURI(it)
                    clearColorFilter()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddConsoleBinding.bind(view)

        setupDatePicker()
        setupImageSelection()
        setupListeners()
    }

    private fun setupImageSelection() {
        binding.btnSelectImage.setOnClickListener {
            val options = arrayOf("Cámara", "Galería")
            AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar Imagen")
                .setItems(options) { _, which ->
                    if (which == 0) openCamera() else galleryLauncher.launch("image/*")
                }.show()
        }
    }

    private fun openCamera() {
        val file = File.createTempFile("console_${System.currentTimeMillis()}", ".jpg", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        currentPhotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        cameraLauncher.launch(currentPhotoUri)
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val company = binding.etCompany.text.toString().trim()
            val price = binding.etPrice.text?.toString()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

            val imageToSave = finalImageUriStr ?: "https://static.vecteezy.com/system/resources/previews/005/337/799/original/icon-image-not-found-free-vector.jpg"

            if (name.isNotEmpty() && date.isNotEmpty() && company.isNotEmpty()) {
                val newConsole = Console(
                    name = name,
                    releasedate = date,
                    company = company,
                    description = binding.etDescription.text.toString(),
                    image = imageToSave,
                    price = price,
                    favorite = binding.swFavorite.isChecked,
                    nativeGames = emptyList(),
                    adaptedGames = emptyList()
                )
                viewModel.addConsole(newConsole)
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Nombre, Compañía y Fecha son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                binding.etDate.setText(String.format("%02d/%02d/%d", d, m + 1, y))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}
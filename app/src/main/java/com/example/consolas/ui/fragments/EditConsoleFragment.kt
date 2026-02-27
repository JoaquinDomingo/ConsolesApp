package com.example.consolas.ui.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Cambiado a activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.databinding.FragmentEditConsoleBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditConsoleFragment : Fragment(R.layout.fragment_edit_console) {

    private lateinit var binding: FragmentEditConsoleBinding
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: EditConsoleFragmentArgs by navArgs()

    private var originalConsole: Console? = null
    private var oldName: String = ""

    private var currentPhotoUri: Uri? = null
    private var currentImageUriStr: String? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { updateImagePreview(it.toString()) }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) currentPhotoUri?.let { updateImagePreview(it.toString()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditConsoleBinding.bind(view)

        setupInitialData()
        setupImageActions()
        setupListeners()
    }

    private fun updateImagePreview(uriStr: String) {
        currentImageUriStr = uriStr
        Glide.with(this).load(uriStr).circleCrop().into(binding.ivEditConsolePreview)
        checkChanges()
    }

    private fun setupInitialData() {
        val nameToFind = args.consoleName

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.find { it.name == nameToFind } ?: return@observe
            if (originalConsole == null) { // Solo cargar la primera vez
                originalConsole = console
                oldName = console.name
                currentImageUriStr = console.image

                binding.apply {
                    etEditName.setText(console.name)
                    etEditCompany.setText(console.company)
                    etEditDate.setText(console.releasedate)
                    etPrice.setText(console.price.toString())
                    etEditDescription.setText(console.description)
                    etEditImage.setText(console.image) // Mantenemos el campo por si acaso
                    swFavorite.isChecked = console.favorite

                    Glide.with(this@EditConsoleFragment)
                        .load(console.image)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .circleCrop()
                        .into(ivEditConsolePreview)

                    btnUpdateConsole.isEnabled = false
                    btnUpdateConsole.alpha = 0.5f
                }
            }
        }
    }

    private fun setupImageActions() {
        binding.btnEditSelectImage.setOnClickListener {
            val options = arrayOf("Cámara", "Galería")
            AlertDialog.Builder(requireContext())
                .setTitle("Cambiar Imagen")
                .setItems(options) { _, which ->
                    if (which == 0) openCamera() else galleryLauncher.launch("image/*")
                }.show()
        }
    }

    private fun openCamera() {
        val file = File.createTempFile("edit_${System.currentTimeMillis()}", ".jpg", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        currentPhotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        cameraLauncher.launch(currentPhotoUri)
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { checkChanges() }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.apply {
            etEditName.addTextChangedListener(watcher)
            etEditCompany.addTextChangedListener(watcher)
            etEditDate.addTextChangedListener(watcher)
            etPrice.addTextChangedListener(watcher)
            etEditDescription.addTextChangedListener(watcher)
            etEditImage.addTextChangedListener(watcher) // Por si editan la URL a mano
            swFavorite.setOnCheckedChangeListener { _, _ -> checkChanges() }

            btnUpdateConsole.setOnClickListener { saveChanges() }
        }
    }

    private fun checkChanges() {
        val original = originalConsole ?: return
        val currentPrice = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0

        val hasChanged =
            binding.etEditName.text.toString() != original.name ||
                    binding.etEditCompany.text.toString() != original.company ||
                    binding.etEditDate.text.toString() != original.releasedate ||
                    binding.etEditDescription.text.toString() != original.description ||
                    currentImageUriStr != original.image || // CAMBIO DETECTADO EN IMAGEN
                    currentPrice != original.price ||
                    binding.swFavorite.isChecked != original.favorite

        binding.btnUpdateConsole.isEnabled = hasChanged
        binding.btnUpdateConsole.alpha = if (hasChanged) 1.0f else 0.5f
    }

    private fun saveChanges() {
        val newName = binding.etEditName.text.toString().trim()
        val price = binding.etPrice.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0

        if (newName.isBlank()) {
            binding.etEditName.error = "El nombre es obligatorio"
            return
        }

        val updatedData = UpdateConsole(
            name = newName,
            releasedate = binding.etEditDate.text.toString(),
            company = binding.etEditCompany.text.toString(),
            description = binding.etEditDescription.text.toString(),
            image = currentImageUriStr ?: originalConsole?.image ?: "",
            price = price,
            favorite = binding.swFavorite.isChecked
        )

        viewModel.editConsole(oldName, updatedData)
        findNavController().popBackStack()
    }
}
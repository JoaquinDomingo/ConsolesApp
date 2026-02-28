package com.example.consolas.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.consolas.R
import com.example.consolas.databinding.FragmentAddGameBinding
import com.example.consolas.domain.model.Game
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import com.example.consolas.ui.views.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class AddGameFragment : Fragment(R.layout.fragment_add_game) {

    private lateinit var binding: FragmentAddGameBinding
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: AddGameFragmentArgs by navArgs()

    private var currentPhotoUri: Uri? = null
    private var finalImageUriStr: String? = null

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) openCamera() else Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            finalImageUriStr = it.toString()
            binding.ivGamePreview.setImageURI(it)
            binding.ivGamePreview.imageTintList = null
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let {
                finalImageUriStr = it.toString()
                binding.ivGamePreview.setImageURI(it)
                binding.ivGamePreview.imageTintList = null
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddGameBinding.bind(view)

        setupDatePicker()
        setupImageSelection()

        binding.btnSaveGame.setOnClickListener {
            val title = binding.etGameTitle.text.toString().trim()
            val date = binding.etGameDate.text.toString().trim()
            val desc = binding.etGameDesc.text.toString().trim()
            val image = finalImageUriStr ?: "https://static.vecteezy.com/system/resources/previews/005/337/799/original/icon-image-not-found-free-vector.jpg"

            if (title.isNotBlank() && date.isNotBlank()) {
                val newGame = Game(
                    title = title,
                    releaseDate = date,
                    description = desc,
                    image = image
                )
                saveGameToConsole(newGame)
            } else {
                Toast.makeText(requireContext(), "El título y la fecha son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupImageSelection() {
        binding.btnSelectGameImage.setOnClickListener {
            val options = arrayOf("Cámara", "Galería")
            AlertDialog.Builder(requireContext())
                .setTitle("Seleccionar Imagen")
                .setItems(options) { _, which ->
                    if (which == 0) checkPermissionAndCamera() else galleryLauncher.launch("image/*")
                }.show()
        }
    }

    private fun checkPermissionAndCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        try {
            val directory = File(requireContext().cacheDir, "game_images")
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, "game_${System.currentTimeMillis()}.jpg")
            currentPhotoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
            cameraLauncher.launch(currentPhotoUri)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al abrir cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDatePicker() {
        binding.etGameDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                binding.etGameDate.setText(String.format("%02d/%02d/%d", d, m + 1, y))
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun saveGameToConsole(game: Game) {
        val consoleName = args.consoleName
        val isNative = args.isNative

        viewModel.addGameToConsole(consoleName, game, isNative)

        // Feedback al usuario mediante la notificación Pop-up que creamos en MainActivity
        (activity as? MainActivity)?.triggerNotification(
            "Guardando Juego",
            "Añadiendo ${game.title} a la colección de $consoleName"
        )

        Toast.makeText(requireContext(), "Enviando al servidor...", Toast.LENGTH_SHORT).show()

        // Regresamos a la pantalla de detalle
        findNavController().popBackStack()
    }
}
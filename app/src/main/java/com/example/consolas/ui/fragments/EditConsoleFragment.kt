package com.example.consolas.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.consolas.R
import com.example.consolas.databinding.FragmentEditConsoleBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditConsoleFragment : Fragment(R.layout.fragment_edit_console) {

    private lateinit var binding: FragmentEditConsoleBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: EditConsoleFragmentArgs by navArgs()

    private var originalConsole: Console? = null
    private var oldName: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditConsoleBinding.bind(view)

        setupInitialData()
        setupListeners()
    }

    private fun setupInitialData() {
        val position = args.consolePosition

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.getOrNull(position) ?: return@observe

            // Guardamos referencia original para comparar cambios
            originalConsole = console
            oldName = console.name

            binding.apply {
                etEditName.setText(console.name)
                etEditCompany.setText(console.company)
                etEditDate.setText(console.releasedate)
                binding.etPrice.setText(console.price.toString())
                etEditDescription.setText(console.description)
                etEditImage.setText(console.image)
                swFavorite.isChecked = console.favorite

                // Habilitamos edición y configuramos estado inicial del botón
                etEditName.isEnabled = true
                btnUpdateConsole.isEnabled = false
                btnUpdateConsole.alpha = 0.5f
            }
        }
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkChanges()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.apply {
            etEditName.addTextChangedListener(watcher)
            etEditCompany.addTextChangedListener(watcher)
            etEditDate.addTextChangedListener(watcher)
            etPrice.addTextChangedListener(watcher)
            etEditDescription.addTextChangedListener(watcher)
            etEditImage.addTextChangedListener(watcher)
            swFavorite.setOnCheckedChangeListener { _, _ -> checkChanges() }

            btnUpdateConsole.setOnClickListener {
                saveChanges()
            }
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
                    binding.etEditImage.text.toString() != original.image ||
                    currentPrice != original.price ||
                    binding.swFavorite.isChecked != original.favorite

        binding.btnUpdateConsole.isEnabled = hasChanged
        binding.btnUpdateConsole.alpha = if (hasChanged) 1.0f else 0.5f
    }

    private fun saveChanges() {
        val newName = binding.etEditName.text.toString().trim()
        val price = binding.etPrice.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0

        if (newName.isBlank()) {
            binding.etEditName.error = "El nombre no puede estar vacío"
            return
        }

        val updatedData = UpdateConsole(
            name = newName,
            releasedate = binding.etEditDate.text.toString(),
            company = binding.etEditCompany.text.toString(),
            description = binding.etEditDescription.text.toString(),
            image = binding.etEditImage.text.toString(),
            price = price,
            favorite = binding.swFavorite.isChecked
        )

        // Enviamos el antiguo nombre como identificador para que el ViewModel
        // pueda gestionar el borrado de la entidad antigua en Room si el nombre cambió
        viewModel.editConsole(oldName, updatedData)

        // Sincronizamos el estado de favorito por seguridad
        viewModel.setFavorite(newName, binding.swFavorite.isChecked)

        findNavController().popBackStack()
    }
}
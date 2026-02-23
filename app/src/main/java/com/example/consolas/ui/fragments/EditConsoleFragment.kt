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

    // Variable para guardar la consola original y comparar
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
            val console = lista?.getOrNull(position)
            console?.let {
                originalConsole = it // Guardamos la referencia original

                oldName = it.name

                binding.etEditName.setText(it.name)
                binding.etEditCompany.setText(it.company)
                binding.etEditDate.setText(it.releasedate)
                binding.etEditDescription.setText(it.description)
                binding.etEditImage.setText(it.image)

                binding.etPrice.setText(it.price.toString())
                binding.swFavorite.isChecked = it.favorite

                //Permite editar el nombre(renombrando).
                binding.etEditName.isEnabled = true

                // Al cargar por primera vez, el botón debe estar desactivado
                binding.btnUpdateConsole.isEnabled = false
                binding.btnUpdateConsole.alpha = 0.5f
            }
        }
    }

    private fun setupListeners() {
        // Creamos un vigilante de texto común
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkChanges()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        // Lo añadimos a todos los campos editables
        binding.etEditCompany.addTextChangedListener(watcher)
        binding.etEditDate.addTextChangedListener(watcher)
        binding.etEditDescription.addTextChangedListener(watcher)
        binding.etEditImage.addTextChangedListener(watcher)

        binding.etEditName.addTextChangedListener(watcher)
        binding.etPrice.addTextChangedListener(watcher)
        binding.swFavorite.setOnCheckedChangeListener { _, _ -> checkChanges() }

        binding.btnUpdateConsole.setOnClickListener {
            saveChanges()
        }
    }

    private fun checkChanges() {
        val original = originalConsole ?: return

        // Comparamos el texto actual con el original
        val hasChanged = 
            binding.etEditName.text.toString() != original.name ||
            binding.etEditCompany.text.toString() != original.company ||
                binding.etEditDate.text.toString() != original.releasedate ||
                binding.etEditDescription.text.toString() != original.description ||
                binding.etEditImage.text.toString() != original.image ||

                price != original.price ||
                binding.swFavorite.isChecked != original.favorite

        // Activamos o desactivamos el botón según el resultado
        binding.btnUpdateConsole.isEnabled = hasChanged
        binding.btnUpdateConsole.alpha = if (hasChanged) 1.0f else 0.5f
    }

    private fun saveChanges() {
        val name = binding.etEditName.text.toString().trim()

        val price = binding.etPrice.text?.toString()?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
        if (newName.isBlank()) {
            binding.etEditName.error = "El nombre no puede estar vacío"
            return
        }

        val updatedData = UpdateConsole(
            name = newName,

            releasedate = binding.etEditDate.text.toString(),
            company = binding.etEditCompany.text.toString(),
            description = binding.etEditDescription.text.toString(),
            image = binding.etEditImage.text.toString()

            price = price,
            favorite = binding.swFavorite.isChecked
        )

        //Permite renombrar
        val identifier = oldName.ifBlank { newName }

        viewModel.editConsole(identifier, updatedData)

        //Despues de renombrar el nombre valido es el nuevo
        viewModel.setFavorite(newName, binding.swFavorite.isChecked)
        findNavController().popBackStack()
    }
}
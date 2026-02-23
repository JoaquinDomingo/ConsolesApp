package com.example.consolas.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.consolas.R
import com.example.consolas.databinding.FragmentAddConsoleBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddConsoleFragment : Fragment(R.layout.fragment_add_console) {

    private lateinit var binding: FragmentAddConsoleBinding
    private val viewModel: ConsoleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddConsoleBinding.bind(view)

        setupDatePicker()
        setupListeners()
    }

    private fun setupDatePicker() {
        // Bloqueamos el teclado para que solo se use el calendario
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val dayF = String.format("%02d", day)
                    val monthF = String.format("%02d", month + 1)
                    binding.etDate.setText("$dayF/$monthF/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val date = binding.etDate.text.toString()
            val company = binding.etCompany.text.toString()
            val desc = binding.etDescription.text.toString()

            if (name.isNotBlank() && date.isNotBlank()) {
                val newConsole = Console(
                    name = name,
                    releasedate = date,
                    company = company,
                    description = desc,
                    image = "https://static.vecteezy.com/system/resources/previews/005/337/799/original/icon-image-not-found-free-vector.jpg",
                    nativeGames = emptyList(),
                    adaptedGames = emptyList()
                )
                viewModel.addConsole(newConsole)
                findNavController().popBackStack() // Vuelve al CrudFragment automáticamente
            } else {
                Toast.makeText(requireContext(), "Faltan datos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
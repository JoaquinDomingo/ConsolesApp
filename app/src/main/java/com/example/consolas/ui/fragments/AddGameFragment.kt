package com.example.consolas.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Cambiado para compartir el estado del ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.consolas.R
import com.example.consolas.databinding.FragmentAddGameBinding
import com.example.consolas.domain.model.Game
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddGameFragment : Fragment(R.layout.fragment_add_game) {

    private lateinit var binding: FragmentAddGameBinding
    // Usamos activityViewModels para asegurar que accedemos a la misma lista de consolas que el resto de la app
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: AddGameFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddGameBinding.bind(view)

        setupDatePicker()

        binding.btnSaveGame.setOnClickListener {
            val title = binding.etGameTitle.text.toString()
            val date = binding.etGameDate.text.toString()
            val desc = binding.etGameDesc.text.toString()
            val imageUrl = binding.etGameImage.text.toString()

            if (title.isNotBlank() && date.isNotBlank()) {
                val newGame = Game(
                    title = title,
                    releaseDate = date,
                    description = desc,
                    image = imageUrl
                )
                saveGameToConsole(newGame)
            }
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
        // CAMBIO: Ahora buscamos por nombre (consoleName) en lugar de posición
        val consoleName = args.consoleName
        val console = viewModel.consoles.value?.find { it.name == consoleName }

        console?.let { currentConsole ->
            val newNative = currentConsole.nativeGames.toMutableList()
            val newAdapted = currentConsole.adaptedGames.toMutableList()

            if (args.isNative) {
                newNative.add(game)
            } else {
                newAdapted.add(game)
            }

            val updateData = UpdateConsole(
                nativeGames = newNative,
                adaptedGames = newAdapted
            )

            // Usamos el nombre como ID único para la actualización
            viewModel.editConsole(currentConsole.name, updateData)

            findNavController().popBackStack()
        }
    }
}
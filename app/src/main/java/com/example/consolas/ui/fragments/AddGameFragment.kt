package com.example.consolas.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: AddGameFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddGameBinding.bind(view)

        setupDatePicker()

        binding.btnSaveGame.setOnClickListener {
            val title = binding.etGameTitle.text.toString()
            val date = binding.etGameDate.text.toString()
            val desc = binding.etGameDesc.text.toString()
            val imageUrl = binding.etGameImage.text.toString() // Capturamos la URL de la imagen

            if (title.isNotBlank() && date.isNotBlank()) {
                // Creamos el juego con el nuevo campo de imagen
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
        val position = args.consolePosition
        val console = viewModel.consoles.value?.getOrNull(position)

        console?.let { currentConsole ->
            // 1. Obtenemos las listas actuales
            val newNative = currentConsole.nativeGames.toMutableList()
            val newAdapted = currentConsole.adaptedGames.toMutableList()

            if (args.isNative) {
                newNative.add(game)
            } else {
                newAdapted.add(game)
            }

            // 3. Creamos el objeto UpdateConsole con las listas modificadas
            val updateData = UpdateConsole(
                nativeGames = newNative,
                adaptedGames = newAdapted
            )

            // 4. Enviamos la actualización al ViewModel usando el nombre como ID
            viewModel.editConsole(currentConsole.name, updateData)

            // 5. Volvemos a la pantalla anterior (la lista de juegos)
            findNavController().popBackStack()
        }
    }
}
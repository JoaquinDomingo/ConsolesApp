package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consolas.R
import com.example.consolas.databinding.FragmentAdaptedGamesBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.ui.adapter.GameAdapter
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdaptedGamesFragment : Fragment(R.layout.fragment_adapted_games) {

    private lateinit var binding: FragmentAdaptedGamesBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: AdaptedGamesFragmentArgs by navArgs()

    // Lista de referencia para el filtrado
    private var allGames: List<Game> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdaptedGamesBinding.bind(view)

        setupRecyclerView()
        setupSearch()

        binding.btnAddGameFloating.setOnClickListener {
            val action = AdaptedGamesFragmentDirections.actionAdaptedGamesFragmentToAddGameFragment(
                args.consolePosition, false
            )
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.rvGames.layoutManager = LinearLayoutManager(requireContext())

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.getOrNull(args.consolePosition)
            console?.let { item ->
                allGames = item.adaptedGames
                // Si no hay texto en el buscador, mostramos la lista completa
                if (binding.etSearchGame.text.isNullOrEmpty()) {
                    updateAdapter(allGames, item)
                }
            }
        }
    }

    private fun setupSearch() {
        // Escuchamos los cambios en el EditText de búsqueda
        binding.etSearchGame.addTextChangedListener { editable ->
            val query = editable.toString().lowercase().trim()
            val filteredList = allGames.filter { it.title.lowercase().contains(query) }

            val currentConsole = viewModel.consoles.value?.getOrNull(args.consolePosition)
            currentConsole?.let { updateAdapter(filteredList, it) }
        }
    }

    private fun updateAdapter(displayList: List<Game>, console: Console) {
        binding.rvGames.adapter = GameAdapter(
            list = displayList,
            onClick = { pos ->
                // Obtenemos el índice real para que el detalle no muestre el juego equivocado
                val realIndex = console.adaptedGames.indexOf(displayList[pos])
                val action = AdaptedGamesFragmentDirections.actionAdaptedGamesFragmentToGameDetailFragment(
                    consolePosition = args.consolePosition,
                    gamePosition = realIndex,
                    isNative = false
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { pos ->
                val realIndex = console.adaptedGames.indexOf(displayList[pos])
                showDeleteConfirmation(console, realIndex)
            }
        )
    }

    private fun showDeleteConfirmation(console: Console, position: Int) {
        val gameName = console.adaptedGames[position].title

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar juego")
            .setMessage("¿Estás seguro de que quieres eliminar \"$gameName\"?")
            .setPositiveButton("ELIMINAR") { _, _ ->
                val newList = console.adaptedGames.toMutableList()
                newList.removeAt(position)
                val update = UpdateConsole(nativeGames = console.nativeGames, adaptedGames = newList)
                viewModel.editConsole(console.name, update)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
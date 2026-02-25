package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Cambiado a activityViewModels para compartir datos
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
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
    // Usamos activityViewModels para que todos los fragmentos vean la misma lista de consolas
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: AdaptedGamesFragmentArgs by navArgs()

    private var allGames: List<Game> = emptyList()
    private var gameAdapter: GameAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdaptedGamesBinding.bind(view)

        setupRecyclerView()
        setupSearch()

        binding.btnAddGameFloating.setOnClickListener {
            // Ahora pasamos consoleName (String)
            val action = AdaptedGamesFragmentDirections.actionAdaptedGamesFragmentToAddGameFragment(
                args.consoleName, false
            )
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 3)

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            // Buscamos la consola por NOMBRE en lugar de posición
            val console = lista?.find { it.name == args.consoleName }
            console?.let { item ->
                allGames = item.adaptedGames
                if (binding.etSearchGame.text.isNullOrEmpty()) {
                    initOrUpdateAdapter(allGames, item)
                }
            }
        }
    }

    private fun setupSearch() {
        binding.etSearchGame.addTextChangedListener { editable ->
            val query = editable.toString().lowercase().trim()
            val filteredList = allGames.filter { it.title.lowercase().contains(query) }

            val currentConsole = viewModel.consoles.value?.find { it.name == args.consoleName }
            currentConsole?.let { initOrUpdateAdapter(filteredList, it) }
        }
    }

    private fun initOrUpdateAdapter(displayList: List<Game>, console: Console) {
        gameAdapter = GameAdapter(
            list = displayList,
            onClick = { pos ->
                // Obtenemos el título del juego seleccionado
                val gameSelected = displayList[pos]
                val action = AdaptedGamesFragmentDirections.actionAdaptedGamesFragmentToGameDetailFragment(
                    args.consoleName, gameSelected.title, false // Enviamos Título (String)
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { pos ->
                showDeleteConfirmation(console, displayList[pos])
            }
        )
        binding.rvGames.adapter = gameAdapter
    }

    private fun showDeleteConfirmation(console: Console, game: Game) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar juego adaptado")
            .setMessage("¿Estás seguro de que quieres eliminar \"${game.title}\"?")
            .setPositiveButton("ELIMINAR") { _, _ ->
                val newList = console.adaptedGames.toMutableList()
                // Borramos comparando por el objeto/título, no por índice
                newList.removeAll { it.title == game.title }

                val update = UpdateConsole(nativeGames = console.nativeGames, adaptedGames = newList)
                viewModel.editConsole(console.name, update)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
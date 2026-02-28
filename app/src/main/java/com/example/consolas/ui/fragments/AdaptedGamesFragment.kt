package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: AdaptedGamesFragmentArgs by navArgs()

    private val gameAdapter: GameAdapter by lazy {
        GameAdapter(
            list = emptyList(),
            onClick = { position ->
                val game = gameAdapter.getCurrentList()[position]
                val action = AdaptedGamesFragmentDirections.actionAdaptedGamesFragmentToGameDetailFragment(
                    args.consoleName, game.title, false
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { position ->
                val game = gameAdapter.getCurrentList()[position]
                viewModel.consoles.value?.find { it.name == args.consoleName }?.let {
                    showDeleteConfirmation(it, game)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAdaptedGamesBinding.bind(view)

        // Configuración del RecyclerView optimizada
        binding.rvGames.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = gameAdapter
            itemAnimator = null
        }

        setupObservers()
        setupSearch()

        binding.btnAddGameFloating.setOnClickListener {
            val action = AdaptedGamesFragmentDirections.actionAdaptedGamesFragmentToAddGameFragment(
                args.consoleName, false
            )
            findNavController().navigate(action)
        }
    }

    private fun setupObservers() {
        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.find { it.name == args.consoleName }
            console?.let { item ->
                android.util.Log.d("DEBUG_UI", "Adapted Fragment recibió ${item.adaptedGames.size} juegos")
                renderList(item.adaptedGames)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearchGame.addTextChangedListener {
            viewModel.consoles.value?.find { it.name == args.consoleName }?.let {
                renderList(it.adaptedGames)
            }
        }
    }

    private fun renderList(games: List<Game>) {
        val query = binding.etSearchGame.text.toString().lowercase().trim()
        val filteredList = if (query.isEmpty()) {
            games
        } else {
            games.filter { it.title.lowercase().contains(query) }
        }

        gameAdapter.updateList(filteredList)
    }

    private fun showDeleteConfirmation(console: Console, game: Game) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar juego adaptado")
            .setMessage("¿Estás seguro de que quieres eliminar \"${game.title}\"?")
            .setPositiveButton("ELIMINAR") { _, _ ->
                val newList = console.adaptedGames.toMutableList()
                newList.removeAll { it.title == game.title }
                val update = UpdateConsole(
                    nativeGames = console.nativeGames,
                    adaptedGames = newList
                )
                viewModel.editConsole(console.name, update)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
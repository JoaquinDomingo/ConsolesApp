package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Cambiado a activityViewModels para consistencia de datos
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.consolas.R
import com.example.consolas.databinding.FragmentNativeGamesBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.domain.model.Game
import com.example.consolas.domain.model.UpdateConsole
import com.example.consolas.ui.adapter.GameAdapter
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NativeGamesFragment : Fragment(R.layout.fragment_native_games) {

    private lateinit var binding: FragmentNativeGamesBinding
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: NativeGamesFragmentArgs by navArgs()

    private val gameAdapter: GameAdapter by lazy {
        GameAdapter(
            list = emptyList(),
            onClick = { position ->
                val game = gameAdapter.getCurrentList()[position]
                val action = NativeGamesFragmentDirections.actionNativeGamesFragmentToGameDetailFragment(
                    args.consoleName, game.title, true
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
        binding = FragmentNativeGamesBinding.bind(view)

        binding.rvGames.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = gameAdapter
            // Evita que el parpadeo de animaciones por defecto interfiera
            itemAnimator = null
        }

        setupObservers()
        setupSearch()

        binding.btnAddGameFloating.setOnClickListener {
            val action = NativeGamesFragmentDirections.actionNativeGamesFragmentToAddGameFragment(
                args.consoleName, true
            )
            findNavController().navigate(action)
        }
    }

    private fun setupObservers() {
        // Al volver del AddGameFragment, este observer DEBE recibir la lista con 11 juegos
        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.find { it.name == args.consoleName }
            console?.let { item ->
                android.util.Log.d("DEBUG_UI", "Fragment recibió ${item.nativeGames.size} juegos")
                renderList(item.nativeGames)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearchGame.addTextChangedListener {
            viewModel.consoles.value?.find { it.name == args.consoleName }?.let {
                renderList(it.nativeGames)
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

        // El DiffUtil dentro de updateList es el que hace la magia
        gameAdapter.updateList(filteredList)
    }

    private fun showDeleteConfirmation(console: Console, game: Game) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar juego")
            .setMessage("¿Estás seguro de que quieres eliminar \"${game.title}\"?")
            .setPositiveButton("ELIMINAR") { _, _ ->
                val newList = console.nativeGames.toMutableList()
                newList.removeAll { it.title == game.title }
                val update = UpdateConsole(nativeGames = newList, adaptedGames = console.adaptedGames)
                viewModel.editConsole(console.name, update)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
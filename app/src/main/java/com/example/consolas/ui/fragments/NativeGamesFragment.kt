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
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: NativeGamesFragmentArgs by navArgs()

    // Variable para guardar la lista original y poder filtrar
    private var allGames: List<Game> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNativeGamesBinding.bind(view)

        setupRecyclerView()
        setupSearch()

        binding.btnAddGameFloating.setOnClickListener {
            val action = NativeGamesFragmentDirections.actionNativeGamesFragmentToAddGameFragment(
                args.consolePosition, true
            )
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.rvGames.layoutManager = LinearLayoutManager(requireContext())

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.getOrNull(args.consolePosition)
            console?.let { item ->
                allGames = item.nativeGames
                // Si el buscador está vacío, mostramos la lista completa
                if (binding.etSearchGame.text.isNullOrEmpty()) {
                    updateAdapter(allGames, item)
                }
            }
        }
    }

    private fun setupSearch() {
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
                // Buscamos el índice real en la lista original para que el detalle no falle
                val realIndex = console.nativeGames.indexOf(displayList[pos])
                val action = NativeGamesFragmentDirections.actionNativeGamesFragmentToGameDetailFragment(
                    args.consolePosition, realIndex, true
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { pos ->
                val realIndex = console.nativeGames.indexOf(displayList[pos])
                showDeleteConfirmation(console, realIndex)
            }
        )
    }

    private fun showDeleteConfirmation(console: Console, position: Int) {
        val gameName = console.nativeGames[position].title

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar juego")
            .setMessage("¿Estás seguro de que quieres eliminar \"$gameName\"?")
            .setPositiveButton("ELIMINAR") { _, _ ->
                val newList = console.nativeGames.toMutableList()
                newList.removeAt(position)
                val update = UpdateConsole(nativeGames = newList, adaptedGames = console.adaptedGames)
                viewModel.editConsole(console.name, update)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
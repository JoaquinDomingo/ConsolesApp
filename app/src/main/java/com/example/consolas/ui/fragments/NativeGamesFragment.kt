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
    // Usamos activityViewModels para asegurar que compartimos la misma instancia de datos
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: NativeGamesFragmentArgs by navArgs()

    private var allGames: List<Game> = emptyList()
    private var gameAdapter: GameAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNativeGamesBinding.bind(view)

        setupRecyclerView()
        setupSearch()

        binding.btnAddGameFloating.setOnClickListener {
            // CORRECCIÓN: Pasamos consoleName (String)
            val action = NativeGamesFragmentDirections.actionNativeGamesFragmentToAddGameFragment(
                args.consoleName, true
            )
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 3)

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            // Búsqueda por nombre único
            val console = lista?.find { it.name == args.consoleName }
            console?.let { item ->
                allGames = item.nativeGames

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

            // Buscamos la consola actual para refrescar el adaptador con la lista filtrada
            val currentConsole = viewModel.consoles.value?.find { it.name == args.consoleName }
            currentConsole?.let { initOrUpdateAdapter(filteredList, it) }
        }
    }

    private fun initOrUpdateAdapter(displayList: List<Game>, console: Console) {
        gameAdapter = GameAdapter(
            list = displayList,
            onClick = { pos ->
                // Obtenemos el título del juego seleccionado (String)
                val gameSelected = displayList[pos]
                val action = NativeGamesFragmentDirections.actionNativeGamesFragmentToGameDetailFragment(
                    args.consoleName, gameSelected.title, true
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { pos ->
                // Pasamos el objeto juego directamente para evitar errores de índice al filtrar
                showDeleteConfirmation(console, displayList[pos])
            }
        )
        binding.rvGames.adapter = gameAdapter
    }

    private fun showDeleteConfirmation(console: Console, game: Game) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar juego")
            .setMessage("¿Estás seguro de que quieres eliminar \"${game.title}\"?")
            .setPositiveButton("ELIMINAR") { _, _ ->
                val newList = console.nativeGames.toMutableList()
                // Borramos por título para asegurar que borramos el correcto incluso si hay filtro
                newList.removeAll { it.title == game.title }

                val update = UpdateConsole(nativeGames = newList, adaptedGames = console.adaptedGames)
                viewModel.editConsole(console.name, update)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
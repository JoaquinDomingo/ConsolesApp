package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consolas.R
import com.example.consolas.databinding.FragmentCrudBinding
import com.example.consolas.ui.adapter.AdapterConsolas
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrudFragment : Fragment(R.layout.fragment_crud) {

    private lateinit var binding: FragmentCrudBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private lateinit var adapter: AdapterConsolas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCrudBinding.bind(view)

        setupRecyclerView()

        binding.btnAdd.setOnClickListener {
            val action = CrudFragmentDirections.actionCrudFragmentToAddConsoleFragment()
            findNavController().navigate(action)
        }

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            adapter.updateList(lista)
        }
    }

    private fun setupRecyclerView() {
        adapter = AdapterConsolas(
            deleteOnClick = { position -> confirmDelete(position) },
            editOnClick = { position ->
                val action = CrudFragmentDirections.actionCrudFragmentToEditConsoleFragment(position)
                findNavController().navigate(action)
            },
            detailOnClick = { position ->
                val action = CrudFragmentDirections.actionCrudFragmentToDetailFragment(position)
                findNavController().navigate(action)
            }
        )
        binding.rvConsoles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConsoles.adapter = adapter
    }

    private fun confirmDelete(position: Int) {
        val console = viewModel.consoles.value?.get(position) ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Consola")
            .setMessage("¿Estás seguro de que deseas borrar ${console.name}?")
            .setPositiveButton("BORRAR") { _, _ ->
                viewModel.deleteConsole(console.name)
            }
            .setNegativeButton("CANCELAR", null)
            .create()
            .apply {
                show()
                // Opcional: Cambiar color del botón borrar a rojo
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.rojo_borrar, null))
            }
    }
}
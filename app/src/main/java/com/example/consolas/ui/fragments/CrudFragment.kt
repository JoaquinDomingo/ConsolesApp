package com.example.consolas.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consolas.R
import com.example.consolas.databinding.FragmentCrudBinding
import com.example.consolas.ui.adapter.AdapterConsolas
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import com.example.consolas.domain.model.Console
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrudFragment : Fragment(R.layout.fragment_crud) {

    private lateinit var binding: FragmentCrudBinding
    private val viewModel: ConsoleViewModel by activityViewModels()
    private lateinit var adapter: AdapterConsolas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCrudBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupButtons()

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            Log.d("DEBUG_UI", "Lista actualizada en Adapter: ${lista.size}")
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.rvConsoles.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE

                val tieneDatos = (viewModel.consoles.value?.isNotEmpty() == true)
                binding.rvConsoles.visibility = if (tieneDatos) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = AdapterConsolas(
            deleteOnClick = { console -> confirmDelete(console) },
            editOnClick = { console ->
                val action = CrudFragmentDirections.actionCrudFragmentToEditConsoleFragment(console.name)
                findNavController().navigate(action)
            },
            detailOnClick = { console ->
                val action = CrudFragmentDirections.actionCrudFragmentToDetailFragment(console.name)
                findNavController().navigate(action)
            }
        )
        binding.rvConsoles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConsoles.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearchConsole.addTextChangedListener { editable ->
            val query = editable.toString().lowercase().trim()
            val currentList = viewModel.consoles.value ?: emptyList()

            val filtered = if (query.isEmpty()) {
                currentList
            } else {
                currentList.filter {
                    it.name.lowercase().contains(query) ||
                            it.company.lowercase().contains(query)
                }
            }
            adapter.submitList(filtered)
        }
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_crudFragment_to_addConsoleFragment)
        }
    }

    private fun confirmDelete(console: Console) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Consola")
            .setMessage("¿Estás seguro de que quieres borrar \"${console.name}\"?")
            .setPositiveButton("BORRAR") { _, _ ->
                viewModel.deleteConsole(console.name)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
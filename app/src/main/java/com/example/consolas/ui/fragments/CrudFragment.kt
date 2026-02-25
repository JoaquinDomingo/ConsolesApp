package com.example.consolas.ui.fragments

import android.os.Bundle
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
    private var fullList: List<Console> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCrudBinding.bind(view)

        setupRecyclerView()
        setupSearch()

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            fullList = lista ?: emptyList()
            filterList(binding.etSearchConsole.text.toString())
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(CrudFragmentDirections.actionCrudFragmentToAddConsoleFragment())
        }
    }

    private fun setupSearch() {
        binding.etSearchConsole.addTextChangedListener { filterList(it.toString()) }
    }

    private fun filterList(query: String) {
        val q = query.lowercase().trim()
        val filtered = if (q.isEmpty()) fullList else fullList.filter {
            it.name.lowercase().contains(q) || it.company.lowercase().contains(q)
        }
        adapter.submitList(filtered)
    }

    private fun setupRecyclerView() {
        adapter = AdapterConsolas(
            deleteOnClick = { console -> confirmDelete(console) },
            editOnClick = { console ->
                // Pasamos el NOMBRE como argumento String
                val action = CrudFragmentDirections.actionCrudFragmentToEditConsoleFragment(console.name)
                findNavController().navigate(action)
            },
            detailOnClick = { console ->
                // Pasamos el NOMBRE como argumento String
                val action = CrudFragmentDirections.actionCrudFragmentToDetailFragment(console.name)
                findNavController().navigate(action)
            }
        )
        binding.rvConsoles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConsoles.adapter = adapter
    }

    private fun confirmDelete(console: Console) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Consola")
            .setMessage("¿Borrar ${console.name}?")
            .setPositiveButton("BORRAR") { _, _ -> viewModel.deleteConsole(console.name) }
            .setNegativeButton("CANCELAR", null)
            .show()
    }
}
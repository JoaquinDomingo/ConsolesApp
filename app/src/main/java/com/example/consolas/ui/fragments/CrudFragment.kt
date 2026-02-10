package com.example.consolas.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consolas.R
import com.example.consolas.databinding.FragmentCrudBinding
import com.example.consolas.domain.model.Console
import com.example.consolas.ui.adapter.AdapterConsolas
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrudFragment : androidx.fragment.app.Fragment(R.layout.fragment_crud) {

    private lateinit var binding: FragmentCrudBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private lateinit var adapter: AdapterConsolas

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCrudBinding.bind(view)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = AdapterConsolas(
            deleteOnClick = { position ->
                val console = viewModel.consoles.value?.get(position)
                console?.let { viewModel.deleteConsole(it) }
            },
            editOnClick = { position ->
                val console = viewModel.consoles.value?.get(position)
                console?.let { mostrarEditConsola(position, it) }
            },
            detailOnClick = { position ->
                val action = CrudFragmentDirections.actionCrudFragmentToDetailFragment(position)
                findNavController().navigate(action)
            }
        )

        binding.myRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CrudFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.consoles.observe(viewLifecycleOwner) { list ->
            adapter.updateList(list)
        }
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            mostrarAddConsola()
        }
    }

    private fun mostrarAddConsola() {
        val layout = createFormLayout("", "", "", "")

        AlertDialog.Builder(requireContext())
            .setTitle("Añadir Nueva Consola")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val console = extractConsoleFromLayout(layout, "default_image")
                if (console != null) {
                    viewModel.addConsole(console)
                } else {
                    Toast.makeText(requireContext(), "Campos obligatorios vacíos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarEditConsola(pos: Int, console: Console) {
        val layout = createFormLayout(console.name, console.releasedate, console.company, console.description)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Consola")
            .setView(layout)
            .setPositiveButton("Guardar Cambios") { _, _ ->
                val updatedConsole = extractConsoleFromLayout(layout, console.image)
                if (updatedConsole != null) {
                    viewModel.editConsole(pos, updatedConsole)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createFormLayout(name: String, date: String, company: String, desc: String): LinearLayout {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(EditText(context).apply { hint = "Nombre"; setText(name) })
            addView(EditText(context).apply { hint = "Fecha"; setText(date) })
            addView(EditText(context).apply { hint = "Compañía"; setText(company) })
            addView(EditText(context).apply { hint = "Descripción"; setText(desc) })
        }
    }

    private fun extractConsoleFromLayout(layout: LinearLayout, currentImage: String): Console? {
        val name = (layout.getChildAt(0) as EditText).text.toString()
        val date = (layout.getChildAt(1) as EditText).text.toString()
        val comp = (layout.getChildAt(2) as EditText).text.toString()
        val desc = (layout.getChildAt(3) as EditText).text.toString()

        return if (name.isNotBlank() && date.isNotBlank()) {
            Console(name, date, comp, desc, currentImage)
        } else null
    }
}
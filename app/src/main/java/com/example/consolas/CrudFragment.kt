package com.example.consolas

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.controller.Controller
import com.example.consolas.interfaces.CrudInteractionListener
import com.example.consolas.databinding.FragmentCrudBinding
import com.example.consolas.models.Console

class CrudFragment : Fragment(), CrudInteractionListener {

    private var _binding: FragmentCrudBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: Controller

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun init() {
        val context = requireContext()

        controller = Controller(context, this)

        initRecyclerView()
        controller.setAdapter()

        binding.btnAdd.setOnClickListener {
            mostrarAddConsola()
        }
    }

    private fun initRecyclerView() {
        binding.myRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    override fun getRecyclerView(): RecyclerView {
        return binding.myRecyclerView
    }

    override fun onStartEditConsole(pos: Int, console: Console) {
        mostrarEditConsola(pos, console)
    }


    fun mostrarAddConsola() {
        val layout = add(name = "", date = "", company = "", description = "")

        val inputName = layout.getChildAt(0) as EditText
        val inputReleaseDate = layout.getChildAt(1) as EditText
        val inputCompany = layout.getChildAt(2) as EditText
        val inputDescription = layout.getChildAt(3) as EditText

        AlertDialog.Builder(requireContext())
            .setTitle("Añadir Nueva Consola")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val name = inputName.text.toString()
                val date = inputReleaseDate.text.toString()
                val company = inputCompany.text.toString()

                if (name.isNotBlank() && date.isNotBlank() && company.isNotBlank()) {
                    val newConsole = Console(name = name, releasedate = date, company = company, description = inputDescription.text.toString(), image = "default_image")
                    controller.addConsole(newConsole)
                } else {
                    Toast.makeText(requireContext(), "Rellena al menos Nombre, Fecha y Compañía", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }



    fun mostrarEditConsola(pos: Int, console: Console) {
        val layout = add(name = console.name, date = console.releasedate, company = console.company, description = console.description)

        val inputName = layout.getChildAt(0) as EditText
        val inputReleaseDate = layout.getChildAt(1) as EditText
        val inputCompany = layout.getChildAt(2) as EditText
        val inputDescription = layout.getChildAt(3) as EditText

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Consola: ${console.name}")
            .setView(layout)
            .setPositiveButton("Guardar Cambios") { _, _ ->
                val name = inputName.text.toString()
                val date = inputReleaseDate.text.toString()
                val company = inputCompany.text.toString()
                val description = inputDescription.text.toString()

                if (name.isNotBlank() && date.isNotBlank() && company.isNotBlank()) {
                    val updatedConsole = Console(name = name, releasedate = date, company = company, description = description, image = console.image)
                    controller.editConsole(pos, updatedConsole)
                } else {
                    Toast.makeText(requireContext(), "Rellena al menos Nombre, Fecha y Compañía", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }


    private fun add(name: String, date: String, company: String, description: String): LinearLayout {
        val context = requireContext()

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val inputName = EditText(context).apply { hint = "Nombre de la Consola"; setText(name) }
        val inputReleaseDate = EditText(context).apply { hint = "Fecha de Lanzamiento"; setText(date) }
        val inputCompany = EditText(context).apply { hint = "Compañía"; setText(company) }
        val inputDescription = EditText(context).apply { hint = "Descripción"; setText(description) }

        layout.addView(inputName)
        layout.addView(inputReleaseDate)
        layout.addView(inputCompany)
        layout.addView(inputDescription)

        return layout
    }
}
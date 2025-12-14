package com.example.consolas

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consolas.controller.Controller
import com.example.consolas.databinding.ActivityMainBinding
import com.example.consolas.models.Console

class MainActivity : AppCompatActivity() {
    lateinit var controller : Controller
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        initRecyclerView()
        controller = Controller(this)
        controller.setAdapter()

        binding.btnAdd.setOnClickListener {
            mostrarAddConsola()
        }
    }

    private fun initRecyclerView(){
        binding.myRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun mostrarAddConsola(){
        val layout = add(
            name = "", date = "", company = "", description = ""
        )

        val inputName = layout.getChildAt(0) as EditText
        val inputReleaseDate = layout.getChildAt(1) as EditText
        val inputCompany = layout.getChildAt(2) as EditText
        val inputDescription = layout.getChildAt(3) as EditText

        AlertDialog.Builder(this)
            .setTitle("Añadir Nueva Consola")
            .setView(layout)
            .setPositiveButton("Guardar") { dialog, _ ->
                val name = inputName.text.toString()
                val date = inputReleaseDate.text.toString()
                val company = inputCompany.text.toString()
                val description = inputDescription.text.toString()

                if (name.isNotBlank() && date.isNotBlank() && company.isNotBlank()) {
                    val newConsole = Console(
                        name = name,
                        releasedate = date,
                        company = company,
                        description = description,
                        image = "default_image"
                    )
                    controller.addConsole(newConsole)
                } else {
                    Toast.makeText(this, "Rellena al menos Nombre, Fecha y Compañía", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    fun mostrarEditConsola(pos: Int, console: Console){
        val layout = add(
            name = console.name,
            date = console.releasedate,
            company = console.company,
            description = console.description
        )

        val inputName = layout.getChildAt(0) as EditText
        val inputReleaseDate = layout.getChildAt(1) as EditText
        val inputCompany = layout.getChildAt(2) as EditText
        val inputDescription = layout.getChildAt(3) as EditText

        AlertDialog.Builder(this)
            .setTitle("Editar Consola: ${console.name}")
            .setView(layout)
            .setPositiveButton("Guardar Cambios") { dialog, _ ->
                val name = inputName.text.toString()
                val date = inputReleaseDate.text.toString()
                val company = inputCompany.text.toString()
                val description = inputDescription.text.toString()

                if (name.isNotBlank() && date.isNotBlank() && company.isNotBlank()) {
                    val updatedConsole = Console(
                        name = name,
                        releasedate = date,
                        company = company,
                        description = description,
                        image = console.image
                    )
                    controller.editConsole(pos, updatedConsole)
                } else {
                    Toast.makeText(this, "Rellena al menos Nombre, Fecha y Compañía", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun add(name: String, date: String, company: String, description: String): LinearLayout {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val inputName = EditText(this).apply {
            hint = "Nombre de la Consola"
            setText(name)
        }
        val inputReleaseDate = EditText(this).apply {
            hint = "Fecha de Lanzamiento"
            setText(date)
        }
        val inputCompany = EditText(this).apply {
            hint = "Compañía"
            setText(company)
        }
        val inputDescription = EditText(this).apply {
            hint = "Descripción"
            setText(description)
        }

        layout.addView(inputName)
        layout.addView(inputReleaseDate)
        layout.addView(inputCompany)
        layout.addView(inputDescription)

        return layout
    }
}
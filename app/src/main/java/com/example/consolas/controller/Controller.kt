package com.example.consolas.controller

import android.content.Context
import android.widget.Toast
import com.example.consolas.adapter.AdapterConsolas
import com.example.consolas.dao.DaoConsolas
import com.example.consolas.interfaces.CrudInteractionListener
import com.example.consolas.models.Console

class Controller(private val context: Context, private val listener: CrudInteractionListener) {
    lateinit var listConsoles : MutableList<Console>
    lateinit var adapterConsole: AdapterConsolas

    init {
        initData()
    }

    fun initData(){
        listConsoles = DaoConsolas.myDao.getDataConsoles().toMutableList()
    }

    fun setAdapter(){
        adapterConsole = AdapterConsolas(
            listConsoles,
            { pos -> delConsole(pos) },
            { pos -> startEditConsole(pos) },
                        { pos -> detailOnClick(pos) }
        )
        val recyclerView = listener.getRecyclerView()
        recyclerView.adapter = adapterConsole
    }

    fun delConsole(pos :Int){
        Toast.makeText(context, "Borrar la consola en la posicion $pos", Toast.LENGTH_SHORT).show()
        listConsoles.removeAt(pos)
        adapterConsole.notifyItemRemoved(pos)
    }

    fun startEditConsole(pos: Int){
        Toast.makeText(context, "Editar la consola en la posicion $pos", Toast.LENGTH_SHORT).show()
        listener.onStartEditConsole(pos, listConsoles[pos])
    }

    fun detailOnClick(pos: Int){
        Toast.makeText(context, "Detalle de la consola en la posicion $pos", Toast.LENGTH_SHORT).show()
        listener.onShowDetail(pos)
    }

    fun editConsole(pos: Int, updatedConsole: Console){
        if (pos >= 0 && pos < listConsoles.size) {
            listConsoles[pos] = updatedConsole
            adapterConsole.notifyItemChanged(pos)
            Toast.makeText(context, "Consola '${updatedConsole.name}' editada con éxito.", Toast.LENGTH_SHORT).show()
        }
    }

    fun addConsole(newConsole: Console){
        listConsoles.add(newConsole)
        adapterConsole.notifyItemInserted(listConsoles.size - 1)
        Toast.makeText(context, "Consola '${newConsole.name}' añadida con éxito.", Toast.LENGTH_SHORT).show()
    }
}
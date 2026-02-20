package com.example.consolas.data.repository

import com.example.consolas.domain.model.Console
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor() {

    private val listConsoles: MutableList<Console> = mutableListOf(

    )


    fun getConsoles(): List<Console> {
        return listConsoles
    }


    fun addConsole(console: Console) {
        listConsoles.add(console)
    }


    fun deleteConsole(console: Console) {
        listConsoles.remove(console)
    }

    fun editConsole(position: Int, console: Console) {
        if (position in listConsoles.indices) {
            listConsoles[position] = console
        }
    }
}
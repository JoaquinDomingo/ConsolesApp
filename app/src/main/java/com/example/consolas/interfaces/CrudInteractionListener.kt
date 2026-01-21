package com.example.consolas.interfaces

import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.models.Console

interface CrudInteractionListener {
    fun onStartEditConsole(pos: Int, console: Console)

    fun getRecyclerView(): RecyclerView

    fun onShowDetail(pos: Int)
}
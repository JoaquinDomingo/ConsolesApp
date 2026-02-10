package com.example.consolas.domain.interfaces

import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.domain.model.Console

interface CrudInteractionListener {
    fun onStartEditConsole(pos: Int, console: Console)

    fun getRecyclerView(): RecyclerView

    fun onShowDetail(pos: Int)
}
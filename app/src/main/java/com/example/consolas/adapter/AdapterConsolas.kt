package com.example.consolas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.R
import com.example.consolas.models.Console

class AdapterConsolas (
    var listConsoles: MutableList<Console>,
    var deleteOnClick: (Int) -> Unit,
    var editOnClick: (Int) -> Unit,
    var detailOnClick: (Int) -> Unit
): RecyclerView.Adapter<ViewHConsolas>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHConsolas {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemConsole = R.layout.item_console
        return ViewHConsolas(
            layoutInflater.inflate(layoutItemConsole, parent, false),
            deleteOnClick,
            editOnClick,
            detailOnClick
        )
    }


    override fun onBindViewHolder(holder: ViewHConsolas, position: Int) {
        holder.renderize(listConsoles.get(position))
    }

    override fun getItemCount(): Int = listConsoles.size

}
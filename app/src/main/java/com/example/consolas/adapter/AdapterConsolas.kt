package com.example.consolas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.R
import com.example.consolas.models.Console

class AdapterConsolas (var listConsoles : MutableList<Console>) :
    RecyclerView.Adapter<ViewHConsolas>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHConsolas {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutItemConsole = R.layout.item_consola
        return ViewHConsolas(layoutInflater.inflate(layoutItemConsole, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHConsolas, position: Int) {
        holder.renderize(listConsoles.get(position))
    }

    override fun getItemCount(): Int = listConsoles.size

}
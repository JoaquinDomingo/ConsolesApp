package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.R
import com.example.consolas.ui.viewHolder.ViewHConsolas
import com.example.consolas.domain.model.Console

class AdapterConsolas(
    private val deleteOnClick: (Int) -> Unit,
    private val editOnClick: (Int) -> Unit,
    private val detailOnClick: (Int) -> Unit
) : RecyclerView.Adapter<ViewHConsolas>() {

    private var listConsoles: List<Console> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHConsolas {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHConsolas(
            layoutInflater.inflate(R.layout.item_console, parent, false),
            deleteOnClick,
            editOnClick,
            detailOnClick
        )
    }

    override fun onBindViewHolder(holder: ViewHConsolas, position: Int) {
        holder.renderize(listConsoles[position])
    }

    override fun getItemCount(): Int = listConsoles.size

    fun updateList(newList: List<Console>) {
        this.listConsoles = newList
        notifyDataSetChanged()
    }
}
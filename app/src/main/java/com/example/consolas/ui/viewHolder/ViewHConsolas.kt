package com.example.consolas.ui.viewHolder

import android.view.View
import android.widget.ImageButton // Cambiado de Button a ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.domain.model.Console

class ViewHConsolas(
    view: View,
    private val deleteOnClick: (Int) -> Unit,
    private val editOnClick: (Int) -> Unit,
    private val detailOnClick: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val ivConsole: ImageView = view.findViewById(R.id.ivConsole)
    private val tvName: TextView = view.findViewById(R.id.tvConsoleName)
    private val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
    private val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

    fun renderize(console: Console) {
        tvName.text = console.name

        Glide.with(ivConsole.context)
            .load(console.image)
            .centerCrop()
            .into(ivConsole)

        btnEdit.setOnClickListener { editOnClick(adapterPosition) }
        btnDelete.setOnClickListener { deleteOnClick(adapterPosition) }
        itemView.setOnClickListener { detailOnClick(adapterPosition) }
    }
}
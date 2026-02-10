package com.example.consolas.ui.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consolas.databinding.ItemConsoleBinding
import com.example.consolas.domain.model.Console

class ViewHConsolas(
    view: View,
    private val deleteOnClick: (Int) -> Unit,
    private val editOnClick: (Int) -> Unit,
    private val detailOnClick: (Int) -> Unit
) : RecyclerView.ViewHolder(view) {

    private val binding = ItemConsoleBinding.bind(view)

    init {
        setOnClickListeners()
    }

    fun renderize(console: Console) {
        binding.txtviewName.text = console.name
        binding.txtviewReleaseDate.text = console.releasedate
        binding.txtviewCompany.text = console.company
        binding.txtviewDescription.text = console.description

        Glide.with(itemView.context)
            .load(console.image)
            .centerCrop()
            .into(binding.ivConsole)
    }

    private fun setOnClickListeners() {
        binding.btnDelete.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) deleteOnClick(position)
        }
        binding.btnEdit.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) editOnClick(position)
        }
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) detailOnClick(position)
        }
    }
}
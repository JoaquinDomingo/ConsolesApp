package com.example.consolas.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consolas.databinding.ItemConsoleBinding
import com.example.consolas.models.Console


class ViewHConsolas (
    view: View,
    var deleteOnClick: (Int) -> Unit,
    var editOnClick: (Int) -> Unit,
    var detailOnClick: (Int) -> Unit
) : RecyclerView.ViewHolder (view) {
    lateinit var  binding : ItemConsoleBinding

    init{
        binding = ItemConsoleBinding.bind(view)
        setOnClickListeners()
    }

    fun renderize(console: Console){
        binding.txtviewName.setText(console.name)
        binding.txtviewReleaseDate.setText(console.releasedate)
        binding.txtviewCompany.setText(console.company)
        binding.txtviewDescription.setText(console.description)
        Glide
            .with(itemView.context)
            .load(console.image)
            .centerCrop()
            .into(binding.ivConsole)
    }

    private  fun setOnClickListeners(){

        binding.btnDelete.setOnClickListener {
            deleteOnClick (adapterPosition)
        }
        binding.btnEdit.setOnClickListener {
            editOnClick (adapterPosition)
        }
        binding.root.setOnClickListener {
            detailOnClick (adapterPosition)
        }
    }
}
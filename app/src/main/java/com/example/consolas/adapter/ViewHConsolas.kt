package com.example.consolas.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consolas.databinding.ItemConsoleBinding
import com.example.consolas.models.Console


class ViewHConsolas (view : View) : RecyclerView.ViewHolder (view) {
    lateinit var  binding : ItemConsoleBinding

    init{
        binding = ItemConsoleBinding.bind(view)
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
}
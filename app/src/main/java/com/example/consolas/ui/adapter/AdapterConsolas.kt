package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consolas.databinding.ItemConsoleBinding
import com.example.consolas.domain.model.Console

class AdapterConsolas(
    private val deleteOnClick: (Console) -> Unit, // Cambiado de Int a Console
    private val editOnClick: (Console) -> Unit,   // Cambiado de Int a Console
    private val detailOnClick: (Console) -> Unit  // Cambiado de Int a Console
) : ListAdapter<Console, AdapterConsolas.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemConsoleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(console: Console) {
            binding.tvConsoleName.text = console.name
            binding.tvConsoleCompany.text = console.company
            binding.tvConsolePrice.text = "${console.price} €"

            Glide.with(binding.root.context)
                .load(console.image)
                .circleCrop()
                .into(binding.ivConsole)

            // Configuración de clics enviando el OBJETO
            binding.btnDelete.setOnClickListener { deleteOnClick(console) }
            binding.btnEdit.setOnClickListener { editOnClick(console) }
            binding.root.setOnClickListener { detailOnClick(console) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemConsoleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Console>() {
        override fun areItemsTheSame(oldItem: Console, newItem: Console) = oldItem.name == newItem.name
        override fun areContentsTheSame(oldItem: Console, newItem: Console) = oldItem == newItem
    }
}
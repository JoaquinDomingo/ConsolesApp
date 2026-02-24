package com.example.consolas.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.consolas.domain.model.Console
class ConsoleDiffCallback : DiffUtil.ItemCallback<Console>() {
    override fun areItemsTheSame(oldItem: Console, newItem: Console): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Console, newItem: Console): Boolean {
        // Al ser data class, esto compara todos los atributos
        return oldItem == newItem
    }
}
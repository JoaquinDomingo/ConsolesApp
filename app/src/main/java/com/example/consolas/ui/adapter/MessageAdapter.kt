package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.databinding.ItemMessageBinding
import com.example.consolas.domain.repository.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.VH>() {

    private val items = mutableListOf<Message>()
    private val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun submit(list: List<Message>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(m: Message) {
            binding.tvMessage.text = m.text
            binding.tvTime.text = sdf.format(Date(m.timestamp))

            val params = binding.card.layoutParams as ViewGroup.MarginLayoutParams
            if (m.fromUser) {
                binding.card.setCardBackgroundColor(0xFFE3F2FD.toInt()) // light blue
                params.marginStart = 64
                params.marginEnd = 8
            } else {
                binding.card.setCardBackgroundColor(0xFFF1F1F1.toInt()) // light gray
                params.marginStart = 8
                params.marginEnd = 64
            }
            binding.card.layoutParams = params
        }
    }
}

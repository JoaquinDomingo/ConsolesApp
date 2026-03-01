package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.databinding.ItemMessageBinding
import com.example.consolas.domain.repository.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val currentUserEmail: String) : RecyclerView.Adapter<MessageAdapter.VH>() {

    private val items = mutableListOf<Message>()
    private val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    fun submit(list: List<Message>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        // CORRECCIÓN: Si el sender es mi email, el mensaje es enviado (SENT)
        return if (items[position].sender == currentUserEmail) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], getItemViewType(position))
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(m: Message, viewType: Int) {
            // CORRECCIÓN: Usamos m.text (o m.message según tu modelo de dominio)
            binding.tvMessage.text = m.text
            binding.tvTime.text = sdf.format(Date(m.timestamp))

            val params = binding.card.layoutParams as ViewGroup.MarginLayoutParams

            if (viewType == TYPE_SENT) {
                binding.card.setCardBackgroundColor(0xFFE3F2FD.toInt()) // Azul claro
                params.marginStart = 120
                params.marginEnd = 16
            } else {
                binding.card.setCardBackgroundColor(0xFFF5F5F5.toInt()) // Gris claro
                params.marginStart = 16
                params.marginEnd = 120
            }

            binding.card.layoutParams = params
        }
    }
}
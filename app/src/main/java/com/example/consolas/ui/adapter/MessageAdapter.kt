package com.example.consolas.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.databinding.ItemMessageBinding
import com.example.consolas.domain.repository.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val myEmail: String) : RecyclerView.Adapter<MessageAdapter.VH>() {

    private val items = mutableListOf<Message>()
    private val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    fun submit(list: List<Message>) {
        // SOLUCIÓN DUPLICADOS: Filtramos mensajes idénticos en la misma milésima
        val distinctList = list.distinctBy { "${it.sender}_${it.timestamp}_${it.text}" }

        items.clear()
        items.addAll(distinctList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].sender == myEmail) TYPE_SENT else TYPE_RECEIVED
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
            binding.tvMessage.text = m.text
            binding.tvTime.text = sdf.format(Date(m.timestamp))

            // SOLUCIÓN ALINEACIÓN: Usamos los parámetros del padre (LinearLayout)
            val params = binding.card.layoutParams as LinearLayout.LayoutParams

            if (viewType == TYPE_SENT) {
                // ENVIADOS: Derecha + Azul
                binding.card.setCardBackgroundColor(0xFFE3F2FD.toInt())
                params.gravity = Gravity.END
                params.setMargins(100, 4, 8, 4)
            } else {
                // RECIBIDOS: Izquierda + Gris
                binding.card.setCardBackgroundColor(0xFFF5F5F5.toInt())
                params.gravity = Gravity.START
                params.setMargins(8, 4, 100, 4)
            }
            binding.card.layoutParams = params
        }
    }
}
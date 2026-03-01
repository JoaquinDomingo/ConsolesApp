package com.example.consolas.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.data.local.MessageEntity
import com.example.consolas.databinding.ItemUserBinding // Asegúrate de que este es el nombre de tu layout de usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConversationAdapter(
    private val onChatClick: (String) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private var conversations: List<MessageEntity> = emptyList()

    // Actualiza la lista cuando Room detecte cambios
    fun submitList(newList: List<MessageEntity>) {
        conversations = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val lastMessage = conversations[position]
        holder.bind(lastMessage)
    }

    override fun getItemCount(): Int = conversations.size

    inner class ConversationViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageEntity) {
            // Mostramos el email de la persona con la que hablamos
            binding.tvUserName.text = item.userEmail

            // Mostramos el contenido del último mensaje (enviado o recibido)
            binding.tvLastMessage.text = item.text

            // Mostramos la hora real del mensaje
            binding.tvTime.text = formatTimestamp(item.timestamp)

            binding.root.setOnClickListener {
                onChatClick(item.userEmail)
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}
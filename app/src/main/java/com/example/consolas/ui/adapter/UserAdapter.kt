package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consolas.data.model.UserResponse
import com.example.consolas.databinding.ItemUserBinding

class UserAdapter(private val onUserClick: (UserResponse) -> Unit) :
    RecyclerView.Adapter<UserAdapter.UserVH>() {

    private var users = listOf<UserResponse>()

    fun submitList(list: List<UserResponse>) {
        users = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserVH(binding)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val user = users[position]

        holder.binding.tvUserName.text = user.name
        // Como es un usuario nuevo, mostramos su email en el lugar del mensaje
        holder.binding.tvLastMessage.text = user.email
        // Ocultamos la hora o ponemos un texto vacío ya que no hay mensajes
        holder.binding.tvTime.text = ""

        holder.binding.root.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount() = users.size

    class UserVH(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)
}
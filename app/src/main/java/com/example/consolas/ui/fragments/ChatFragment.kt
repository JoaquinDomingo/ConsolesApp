package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.consolas.R
import com.example.consolas.databinding.FragmentChatBinding
import com.example.consolas.ui.messages.ConversationAdapter
import com.example.consolas.ui.viewmodels.ConversationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    // Cambiado a ConversationsViewModel (plural) para coincidir con tu archivo anterior
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var adapter: ConversationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentChatBinding.bind(view)

        // 1. Inicializar el Adapter
        // Usamos la acción definida en el nav_graph pasando el email en un Bundle
        adapter = ConversationAdapter { email ->
            val bundle = bundleOf("otherEmail" to email)
            findNavController().navigate(R.id.action_chatFragment_to_mensajesFragment, bundle)
        }

        binding.rvConversations.adapter = adapter

        // 2. Escuchar los cambios en la base de datos (Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.conversations.collect { lista ->
                // Enviamos la lista al adaptador
                adapter.submitList(lista)

                // 3. Manejo del estado vacío
                // Si la lista está vacía, mostramos el TextView de "No hay chats"
                // Asegúrate de tener un TextView con id tvEmpty en fragment_chat.xml
                binding.tvEmpty.isVisible = lista.isEmpty()
                binding.rvConversations.isVisible = lista.isNotEmpty()
            }
        }
    }
}
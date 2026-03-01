package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.consolas.R
import com.example.consolas.databinding.FragmentMensajesBinding
import com.example.consolas.ui.adapter.MessageAdapter
import com.example.consolas.ui.viewmodels.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MensajesFragment : Fragment(R.layout.fragment_mensajes) {

    private lateinit var binding: FragmentMensajesBinding
    private val vm: MessagesViewModel by viewModels()
    private val adapter = MessageAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMensajesBinding.bind(view)

        // 1. Recuperar el email del contacto desde los argumentos del Navigation
        val otherEmail = arguments?.getString("otherEmail") ?: ""

        // Inicializar el chat en el ViewModel (Cargar historial y conectar WebSocket)
        vm.initChat(otherEmail)

        // Configuración del RecyclerView
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter

        // Botón de enviar
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text?.toString().orEmpty()
            if (text.isNotBlank()) {
                vm.send(text) // El VM ya sabe que el receptor es 'otherEmail'
                binding.etMessage.setText("")
            }
        }

        // Observar mensajes (Room + WebSockets vía Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            vm.messages.collect { list ->
                adapter.submit(list)
                if (list.isNotEmpty()) {
                    binding.rvMessages.scrollToPosition(list.size - 1)
                }
            }
        }
    }
}
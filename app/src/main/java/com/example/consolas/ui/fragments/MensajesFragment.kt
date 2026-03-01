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

    // Eliminamos la inicialización inmediata aquí porque necesitamos el email del VM
    private lateinit var adapter: MessageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMensajesBinding.bind(view)

        // 1. Recuperar el email del contacto
        val otherEmail = arguments?.getString("otherEmail") ?: ""

        // 2. Obtener mi email desde el ViewModel e inicializar el Adapter
        val myEmail = vm.getMyEmail()
        adapter = MessageAdapter(myEmail)

        // Inicializar el chat en el ViewModel
        vm.initChat(otherEmail)

        // Configuración del RecyclerView
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true // Los mensajes nuevos aparecen abajo
        }
        binding.rvMessages.adapter = adapter

        // Botón de enviar
        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text?.toString().orEmpty()
            if (text.isNotBlank()) {
                vm.send(text)
                binding.etMessage.setText("")
            }
        }

        // Observar mensajes
        viewLifecycleOwner.lifecycleScope.launch {
            vm.messages.collect { list ->
                adapter.submit(list)
                if (list.isNotEmpty()) {
                    binding.rvMessages.smoothScrollToPosition(list.size - 1)
                }
            }
        }
    }
}
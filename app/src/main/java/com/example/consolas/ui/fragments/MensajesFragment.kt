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

        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvMessages.adapter = adapter

        binding.btnSend.setOnClickListener {
            vm.send(binding.etMessage.text?.toString().orEmpty())
            binding.etMessage.setText("")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            vm.messages.collect { list ->
                adapter.submit(list)
                if (list.isNotEmpty()) binding.rvMessages.scrollToPosition(list.size - 1)
            }
        }
    }
}

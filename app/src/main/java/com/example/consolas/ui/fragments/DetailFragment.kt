package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.databinding.FragmentDetailBinding

import com.example.consolas.domain.model.Console

import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    private var current: Console? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailBinding.bind(view)

        setupData()
        setupButtons()
    }

    private fun setupData() {
        val position = args.consolePosition

        viewModel.consoles.observe(viewLifecycleOwner) { listaConsolas ->
            val console = listaConsolas?.getOrNull(position)
            current = console

            console?.let { item ->
                binding.tvDetailName.text = item.name
                binding.tvDetailCompany.text = item.company
                binding.tvDetailDate.text = "Lanzamiento: ${item.releasedate}"
                
                binding.tvDetailPrice.text = "Precio: ${String.format("%.2f", item.price)} €"

                binding.tvDetailDescription.text = item.description

                binding.btnFavorite.text = if (item.favorite) "En favoritos" else "Marcar favorito"

                Glide.with(this)
                    .load(item.image)
                    .centerCrop()
                    .into(binding.tvDetailImage)
            }
        }
    }

    private fun setupButtons() {
        // Botón para ir a la lista de juegos nativos
        binding.btnGoToNative.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToNativeGamesFragment(
                consolePosition = args.consolePosition
            )
            findNavController().navigate(action)
        }

        // Botón para ir a la lista de juegos adaptados
        binding.btnGoToAdapted.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToAdaptedGamesFragment(
                consolePosition = args.consolePosition
            )
            findNavController().navigate(action)
        }

        
        binding.btnFavorite.setOnClickListener {
            val item = current ?: return@setOnClickListener
            val newValue = !item.favorite
            item.favorite = newValue
            binding.btnFavorite.text = if (newValue) "En favoritos" else "Marcar favorito"
            viewModel.setFavorite(item.name, newValue)
        }

    }
}
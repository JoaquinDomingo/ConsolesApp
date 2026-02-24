package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val viewModel: ConsoleViewModel by activityViewModels()
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
        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            // Si la lista es nula o la posición no existe, no hacemos nada
            val console = lista?.getOrNull(position) ?: return@observe

            // PINTAMOS SIEMPRE (Quitamos el if (current == null))
            binding.tvDetailName.text = console.name
            binding.tvDetailCompany.text = console.company
            binding.tvDetailDate.text = "Lanzamiento: ${console.releasedate}"
            binding.tvDetailPrice.text = "Precio: ${String.format("%.2f", console.price)} €"
            binding.tvDetailDescription.text = console.description

            Glide.with(this)
                .load(console.image)
                .placeholder(R.drawable.placeholder) // Añade un placeholder por si acaso
                .centerCrop()
                .into(binding.tvDetailImage)

            updateFavoriteUI(console.favorite)
            current = console
        }
    }

    private fun updateFavoriteUI(isFavorite: Boolean) {
        binding.btnFavorite.text = if (isFavorite) "Quitar de favoritos" else "Marcar como favorito"
        val icon = if (isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off
        binding.btnFavorite.setIconResource(icon)
    }

    private fun setupButtons() {
        // Favorito
        binding.btnFavorite.setOnClickListener {
            val item = current ?: return@setOnClickListener
            viewModel.setFavorite(item.name, !item.favorite)
        }

        // Navegación a Nativos
        binding.btnGoToNative.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToNativeGamesFragment(args.consolePosition)
            findNavController().navigate(action)
        }

        // Navegación a Adaptados
        binding.btnGoToAdapted.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToAdaptedGamesFragment(args.consolePosition)
            findNavController().navigate(action)
        }
    }
}
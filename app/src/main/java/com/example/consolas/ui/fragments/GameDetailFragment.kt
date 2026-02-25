package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Cambiado a activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.databinding.FragmentGameDetailBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailFragment : Fragment(R.layout.fragment_game_detail) {

    private lateinit var binding: FragmentGameDetailBinding
    // Usamos activityViewModels para compartir la instancia con el resto de la App
    private val viewModel: ConsoleViewModel by activityViewModels()
    private val args: GameDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameDetailBinding.bind(view)

        setupData()
    }

    private fun setupData() {
        // CORRECCIÓN: Usamos consoleName y gameTitle (Strings) en lugar de posiciones
        val nameToFind = args.consoleName
        val titleToFind = args.gameTitle

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            // 1. Buscamos la consola por nombre
            val console = lista?.find { it.name == nameToFind }

            // 2. Buscamos el juego por título dentro de la lista correspondiente (Nativos o Adaptados)
            val game = if (args.isNative) {
                console?.nativeGames?.find { it.title == titleToFind }
            } else {
                console?.adaptedGames?.find { it.title == titleToFind }
            }

            game?.let {
                binding.tvGameDetailTitle.text = it.title
                binding.tvGameDetailDate.text = "Lanzamiento: ${it.releaseDate}"
                binding.tvGameDetailDesc.text = it.description

                Glide.with(this)
                    .load(it.image)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(binding.ivGameDetailImage)
            }
        }
    }
}
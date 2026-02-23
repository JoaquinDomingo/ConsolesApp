package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.databinding.FragmentGameDetailBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameDetailFragment : Fragment(R.layout.fragment_game_detail) {

    private lateinit var binding: FragmentGameDetailBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: GameDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameDetailBinding.bind(view)

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            val console = lista?.getOrNull(args.consolePosition)
            val game = if (args.isNative) {
                console?.nativeGames?.getOrNull(args.gamePosition)
            } else {
                console?.adaptedGames?.getOrNull(args.gamePosition)
            }

            game?.let {
                binding.tvGameDetailTitle.text = it.title
                binding.tvGameDetailDate.text = "Lanzamiento: ${it.releaseDate}"
                binding.tvGameDetailDesc.text = it.description
                Glide.with(this).load(it.image).into(binding.ivGameDetailImage)
            }
        }
    }
}
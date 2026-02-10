package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.databinding.FragmentDetailBinding
import com.example.consolas.ui.viewmodels.ConsoleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var binding: FragmentDetailBinding
    private val viewModel: ConsoleViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailBinding.bind(view)

        setupData()
    }

    private fun setupData() {
        val position = args.consolePosition
        // Obtenemos la consola directamente de la lista que observa el ViewModel
        val console = viewModel.consoles.value?.getOrNull(position)

        console?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailCompany.text = it.company
            binding.tvDetailDate.text = "Lanzamiento: ${it.releasedate}"
            binding.tvDetailDescription.text = it.description

            Glide.with(this)
                .load(it.image)
                .centerCrop()
                .into(binding.tvDetailImage)
        }
    }
}
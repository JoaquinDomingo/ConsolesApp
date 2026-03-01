package com.example.consolas.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
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

        // MODIFICACIÓN CRÍTICA: Forzamos al ViewModel a recargar los datos
        // para que no use la lista antigua que tiene en memoria RAM.
        viewModel.refreshFromApi()

        setupData()
        setupButtons()
    }

    private fun setupData() {
        val nameToFind = args.consoleName

        viewModel.consoles.observe(viewLifecycleOwner) { lista ->
            // Buscamos la consola en la lista actualizada que viene del Repositorio
            val console = lista?.find { it.name == nameToFind } ?: return@observe

            // Log de control para verificar que el objeto ya trae el email correcto
            android.util.Log.d("CHAT_DEBUG", "Mostrando -> Consola: ${console.name} | Propietario: ${console.userEmail}")

            // Asignación de datos a la UI
            binding.tvDetailName.text = console.name
            binding.tvDetailCompany.text = console.company
            binding.tvDetailDate.text = "Lanzamiento: ${console.releasedate}"
            binding.tvDetailPrice.text = "Precio: ${String.format("%.2f", console.price)} €"
            binding.tvDetailDescription.text = console.description

            // Mostrar el autor real que viene de la Base de Datos
            binding.tvDetailUser.text = "Subido por: ${console.userEmail}"

            Glide.with(this)
                .load(console.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
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
        // Botón para iniciar chat con el propietario real (el del campo userEmail)
        binding.btnContactOwner.setOnClickListener {
            val console = current ?: return@setOnClickListener

            // Navegamos al MensajesFragment pasando el email del dueño de la consola
            val bundle = bundleOf("otherEmail" to console.userEmail)
            findNavController().navigate(R.id.action_detailFragment_to_mensajesFragment, bundle)
        }

        binding.btnFavorite.setOnClickListener {
            val item = current ?: return@setOnClickListener
            viewModel.setFavorite(item.name, !item.favorite)
        }

        binding.btnGoToNative.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToNativeGamesFragment(args.consoleName)
            findNavController().navigate(action)
        }

        binding.btnGoToAdapted.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToAdaptedGamesFragment(args.consoleName)
            findNavController().navigate(action)
        }
    }
}
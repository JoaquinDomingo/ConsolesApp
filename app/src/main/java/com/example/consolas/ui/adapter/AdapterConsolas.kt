package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.consolas.R
import com.example.consolas.ui.viewHolder.ViewHConsolas
import com.example.consolas.domain.model.Console

/**
 * Usamos ListAdapter en lugar de RecyclerView.Adapter para que las actualizaciones
 * de favoritos sean automáticas, fluidas y eficientes gracias a DiffUtil.
 */
class AdapterConsolas(
    private val deleteOnClick: (Int) -> Unit,
    private val editOnClick: (Int) -> Unit,
    private val detailOnClick: (Int) -> Unit
) : ListAdapter<Console, ViewHConsolas>(ConsoleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHConsolas {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHConsolas(
            layoutInflater.inflate(R.layout.item_console, parent, false),
            deleteOnClick,
            editOnClick,
            detailOnClick
        )
    }

    override fun onBindViewHolder(holder: ViewHConsolas, position: Int) {
        // ListAdapter usa getItem(position) para acceder a la lista interna
        holder.renderize(getItem(position))
    }

    /**
     * Lógica para comparar elementos. Esto es lo que permite que la estrella
     * cambie al instante cuando el ViewModel emite una nueva lista.
     */
    class ConsoleDiffCallback : DiffUtil.ItemCallback<Console>() {
        override fun areItemsTheSame(oldItem: Console, newItem: Console): Boolean {
            // Se comparan por el nombre (identificador único de la consola)
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Console, newItem: Console): Boolean {
            // Se comparan los objetos enteros. Si el favorito cambió, esto devuelve false
            // y ListAdapter refresca automáticamente ese item.
            return oldItem == newItem
        }
    }
}
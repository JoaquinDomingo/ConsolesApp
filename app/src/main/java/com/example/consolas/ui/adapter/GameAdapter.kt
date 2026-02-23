package com.example.consolas.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consolas.R
import com.example.consolas.domain.model.Game

class GameAdapter(
    private val list: List<Game>,
    private val onClick: (Int) -> Unit,        // Nuevo: Clic para detalle
    private val onDeleteClick: (Int) -> Unit    // Clic largo para borrar
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvGameTitle)
        val date: TextView = view.findViewById(R.id.tvGameDate)
        val desc: TextView = view.findViewById(R.id.tvGameDesc)
        val image: ImageView = view.findViewById(R.id.ivGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(v)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = list[position]
        holder.title.text = game.title
        holder.date.text = game.releaseDate
        holder.desc.text = game.description

        Glide.with(holder.image.context)
            .load(game.image)
            .placeholder(R.drawable.ic_menu_gallery)
            .into(holder.image)

        // Clic normal para ver el detalle
        holder.itemView.setOnClickListener {
            onClick(position)
        }

        // Clic largo para borrar
        holder.itemView.setOnLongClickListener {
            onDeleteClick(position)
            true
        }
    }

    override fun getItemCount() = list.size
}
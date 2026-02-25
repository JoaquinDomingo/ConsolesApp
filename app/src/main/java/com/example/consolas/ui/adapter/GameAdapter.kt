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
    private var list: List<Game>,
    private val onClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvGameTitle)
        val image: ImageView = view.findViewById(R.id.ivGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(v)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = list[position]
        holder.title.text = game.title

        Glide.with(holder.image.context)
            .load(game.image)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.image)

        holder.itemView.setOnClickListener { onClick(position) }
        holder.itemView.setOnLongClickListener {
            onDeleteClick(position)
            true
        }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<Game>) {
        this.list = newList
        notifyDataSetChanged()
    }
}
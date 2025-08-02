package com.practicum.playlistmaker.media.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist

class PlaylistsViewHolder(parent: ViewGroup) : ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.track_list, parent, false)
) {

    private val imgPlaylist = itemView.findViewById<ShapeableImageView>(R.id.imgPlaylist)
    private val namePlaylist = itemView.findViewById<TextView>(R.id.namePlaylist)
    private val tracksCount = itemView.findViewById<TextView>(R.id.tracksCount)

    fun bind(model: Playlist) {
        Glide.with(itemView.context)
            .load(model.imgPath)
            .placeholder(R.drawable.plug_artwork_high)
            .centerCrop()
            .into(imgPlaylist)

        namePlaylist.text = model.namePlaylist
        tracksCount.text = word(model.tracksCount.toInt())
    }

    private fun word(count: Int): String {
        val lastTwoDigits = count % 100
        val lastDigit = count % 10

        val word = when {
            lastTwoDigits in 11..14 -> "треков"
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            else -> "треков"
        }

        return "$count $word"
    }

}

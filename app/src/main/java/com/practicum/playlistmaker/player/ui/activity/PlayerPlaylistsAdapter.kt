package com.practicum.playlistmaker.player.ui.activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist

class PlayerPlaylistsAdapter(private val clickListener: PlaylistClickListener) : RecyclerView.Adapter<PlayerPlaylistViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        return PlayerPlaylistViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistListener(position) }
    }

    fun interface PlaylistClickListener {
        fun onPlaylistListener(position: Int)
    }

}

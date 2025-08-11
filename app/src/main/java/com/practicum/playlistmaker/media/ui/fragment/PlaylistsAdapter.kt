package com.practicum.playlistmaker.media.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist

class PlaylistsAdapter(private val clickListener: playlistClickListener?) : Adapter<PlaylistsViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener{ clickListener?.onPlaylistListener(playlists[position]) }
    }

    fun interface playlistClickListener {
        fun onPlaylistListener(playlist: Playlist)
    }

}

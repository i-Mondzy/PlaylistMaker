package com.practicum.playlistmaker.playlist.ui.fragment

import android.view.ViewGroup
import com.practicum.playlistmaker.search.domain.model.Track
import androidx.recyclerview.widget.RecyclerView.Adapter

class TrackAdapter(private val clickListener: TrackClickListener?): Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{ clickListener?.onTrackClickListener(tracks[position], position) }
        holder.itemView.setOnLongClickListener {
            clickListener?.onTrackLongClickListener(tracks[position], position)
            true
        }
    }

    interface TrackClickListener {
        fun onTrackClickListener(track: Track, position: Int)
        fun onTrackLongClickListener(track: Track, position: Int)
    }

}

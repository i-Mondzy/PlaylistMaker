package com.practicum.playlistmaker.media.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.activity.TrackViewHolder

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
        holder.itemView.setOnClickListener{ clickListener?.onTrackListener(tracks[position], position) }
    }

    fun interface TrackClickListener {
        fun onTrackListener(track: Track, position: Int)
    }

}

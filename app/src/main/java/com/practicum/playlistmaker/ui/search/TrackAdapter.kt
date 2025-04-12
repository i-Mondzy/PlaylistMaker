package com.practicum.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.practicum.playlistmaker.domain.model.Track

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
        holder.itemView.setOnClickListener{ clickListener?.onTrackListener(tracks[position]) }
    }

    fun interface TrackClickListener {
        fun onTrackListener(track: Track)
    }

/*//  Метод для обновления списка треков
    fun updateTrackList(newTrackList: List<Track>) {
        track = newTrackList
        notifyDataSetChanged()
    }*/

}

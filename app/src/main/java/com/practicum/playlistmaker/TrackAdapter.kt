package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class TrackAdapter(var track: List<Track>): Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return track.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(track[position])
    }

/*//  Метод для обновления списка треков
    fun updateTrackList(newTrackList: List<Track>) {
        track = newTrackList
        notifyDataSetChanged()
    }*/

}
package com.practicum.playlistmaker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class TrackViewHolder(itemView: View): ViewHolder(itemView) {

    private val trackName: TextView
    private val artistName: TextView
    private val trackTime: TextView
    private val artworkUrl100: TextView

    init {
        trackName = itemView.findViewById(R.id.trackName)
        artistName = itemView.findViewById(R.id.artistName)
        trackTime = itemView.findViewById(R.id.trackTime)
        artworkUrl100 = itemView.findViewById(R.id.artwork)
    }

    fun bind(model: Track) {
        trackName.text = "${model.trackName}"
        artistName.text = "${model.artistName}"
        trackTime.text = "${model.trackTime}"
    }

}
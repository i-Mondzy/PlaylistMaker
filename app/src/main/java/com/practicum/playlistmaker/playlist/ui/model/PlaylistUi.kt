package com.practicum.playlistmaker.playlist.ui.model

import android.os.Parcelable
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistUi(
    val playlistId: Long,
    val namePlaylist: String,
    val description: String,
    val imgPath: String,
    val trackList: List<Track>,
    var tracksTime: String,
    val tracksCount: String
) : Parcelable

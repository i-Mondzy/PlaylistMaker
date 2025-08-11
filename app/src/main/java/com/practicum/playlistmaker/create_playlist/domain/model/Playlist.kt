package com.practicum.playlistmaker.create_playlist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Long,
    val namePlaylist: String,
    val description: String,
    val imgPath: String,
    val trackList: List<Long>,
    val tracksCount: Long
) : Parcelable

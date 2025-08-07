package com.practicum.playlistmaker.create_playlist.domain.model

data class Playlist(
    val playlistId: Long,
    val namePlaylist: String,
    val description: String,
    val imgPath: String,
    val trackList: List<Long>,
    val tracksCount: Long
)

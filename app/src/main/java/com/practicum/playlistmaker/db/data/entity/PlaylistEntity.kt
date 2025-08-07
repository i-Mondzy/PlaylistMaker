package com.practicum.playlistmaker.db.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long,
    val namePlaylist: String,
    val description: String,
    val imgPath: String,
    val trackList: String,
    val tracksCount: Long
)

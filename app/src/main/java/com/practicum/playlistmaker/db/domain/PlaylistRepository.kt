package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun savePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylist(): Flow<List<Playlist>>

    suspend fun saveTrack(playlistTrack: Track)

    suspend fun deleteTrack(playlistTrack: Track)

    suspend fun clearTable()

}

package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun savePlaylist(playlist: Playlist)

    fun getPlaylist(): Flow<List<Playlist>>

    suspend fun saveTrack(playlistTrack: Track)

    suspend fun deleteTrack(playlistTrack: Track)

}

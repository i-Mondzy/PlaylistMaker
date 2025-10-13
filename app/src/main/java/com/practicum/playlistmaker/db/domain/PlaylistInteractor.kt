package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun savePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylist(playlistId: Long): Flow<Playlist>

    suspend fun saveTrack(playlistTrack: Track)

    fun getTracks(tracks: List<Long>): Flow<List<Track>>

    suspend fun deleteTrack(playlistTrackId: Long)

    suspend fun clearTable()

}

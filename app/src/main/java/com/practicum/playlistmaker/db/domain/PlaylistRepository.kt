package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun savePlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylist(playlistId: Long): Flow<Playlist>

    suspend fun saveTrack(playlistTrack: Track)

    fun getTracks(tracks: List<Long>): Flow<List<Track>>

    suspend fun deleteTrack(trackId: Long, skipPlaylistId: Long? = null)

    suspend fun clearTable()

}

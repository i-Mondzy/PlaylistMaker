package com.practicum.playlistmaker.db.domain.impl

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.db.domain.PlaylistRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(val repository: PlaylistRepository) : PlaylistInteractor {

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> {
        return repository.getPlaylist(playlistId)
    }

    override suspend fun saveTrack(playlistTrack: Track) {
        repository.saveTrack(playlistTrack)
    }

    override fun getTracks(tracks: List<Long>): Flow<List<Track>> {
        return repository.getTracks(tracks)
    }

    override suspend fun deleteTrack(playlistTrackId: Long) {
        repository.deleteTrack(playlistTrackId)
    }

    override suspend fun clearTable() {
        repository.clearTable()
    }

}

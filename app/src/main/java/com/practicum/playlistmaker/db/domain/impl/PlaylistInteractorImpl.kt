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

    override fun getPlaylist(): Flow<List<Playlist>> {
        return repository.getPlaylist()
    }

    override suspend fun saveTrack(playlistTrack: Track) {
        repository.saveTrack(playlistTrack)
    }

    override suspend fun deleteTrack(playlistTrack: Track) {
        repository.deleteTrack(playlistTrack)
    }

}

package com.practicum.playlistmaker.db.domain.impl

import com.practicum.playlistmaker.db.domain.FavoriteInteractor
import com.practicum.playlistmaker.db.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteInteractorImpl(
    private val repository: FavoriteRepository
) : FavoriteInteractor {

    override suspend fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override suspend fun deleteTrack(track: Track) {
        repository.deleteTrack(track)
    }

    override suspend fun getTracks(): Flow<List<Track>> {
        val tracks = repository.getTracks().map { it.reversed() }
        return tracks
    }

}

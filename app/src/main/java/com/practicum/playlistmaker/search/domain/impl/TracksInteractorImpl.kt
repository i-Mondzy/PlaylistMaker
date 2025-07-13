package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override suspend fun searchTracks(text: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(text).map { result ->
            when (result) {
                is Resource.Success -> result
                is Resource.Error -> result
            }
        }
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override suspend fun getTracks(): List<Track> {
        return repository.getTracks()
    }

    override fun clearTracks() {
        repository.clearTracks()
    }

}

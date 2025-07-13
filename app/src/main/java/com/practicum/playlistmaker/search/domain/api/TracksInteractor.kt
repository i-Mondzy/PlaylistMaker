package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    suspend fun searchTracks(text: String): Flow<Resource<List<Track>>>

    fun saveTrack(track: Track)

    suspend fun getTracks() : List<Track>

    fun clearTracks()

}

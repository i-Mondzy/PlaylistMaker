package com.practicum.playlistmaker.db.domain

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun saveTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    suspend fun getTracks(): Flow<List<Track>>

}

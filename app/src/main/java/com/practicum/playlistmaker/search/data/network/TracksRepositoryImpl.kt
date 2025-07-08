package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.local.track.TracksManager
import com.practicum.playlistmaker.search.data.TracksNetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: TracksNetworkClient,
    private val tracksManager: TracksManager
) : TracksRepository {

    override suspend fun searchTracks(text: String): Flow<Resource<List<Track>>> {
        val response = networkClient.doRequest(text)

        return flow {
            when (response.resultCode) {
                200 -> {
                    val tracks = (response as TracksSearchResponse).results.map {
                        Track(
                            it.trackId,
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(tracks))
                }

                else -> {
                    emit(Resource.Error("Произошла сетевая ошибка"))
                }
            }
        }
    }

    override fun saveTrack(track: Track) {
        tracksManager.saveTrack(track)
    }

    override fun getTracks(): List<Track> {
        return tracksManager.getTracks()
    }

    override fun clearTracks() {
        tracksManager.clearTracks()
    }

}

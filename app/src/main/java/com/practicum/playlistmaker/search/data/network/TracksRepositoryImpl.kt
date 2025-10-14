package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.db.data.AppDataBase
import com.practicum.playlistmaker.search.data.local.track.TracksManager
import com.practicum.playlistmaker.search.data.TracksNetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val networkClient: TracksNetworkClient,
    private val tracksManager: TracksManager,
    private val appDataBase: AppDataBase
) : TracksRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> {
        return flow {
            val response = networkClient.doRequest(text)

            when (response.resultCode) {
                200 -> {
                    val tracks = copyFavorites((response as TracksSearchResponse).results.map {
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
                    })
                    emit(Resource.Success(tracks))
                }

                else -> {
                    emit(Resource.Error("Произошла сетевая ошибка"))
                }
            }
        }
    }

    override suspend fun saveTrack(track: Track) {
        tracksManager.saveTrack(track)
    }

    override suspend fun getTracks(): Flow<List<Track>> {
        return flow {
            val tracks = tracksManager.getTracks().first()

            emit(copyFavorites(tracks))
        }
//        return copyFavorites(tracksManager.getTracks())
    }

    override fun clearTracks() {
        tracksManager.clearTracks()
    }

    private suspend fun copyFavorites(tracks: List<Track>): List<Track> {
        /*val tracksId = appDataBase.favoriteTrackDao().getTracksId().map { entities ->
            entities.map { trackId ->
                with(trackId) {
                    trackId.toLong()
                }
            }
        }

        val tracks = tracks.map { entities ->
            entities.map { track ->
                track.copy(isFavorite = track.trackId in tracksId)
            }
        }*/

        val tracksId = appDataBase.favoriteTrackDao().getTracksId().first().map { it.toLong() }
        return tracks.map { track ->
            track.copy(isFavorite = track.trackId in tracksId)
        }
    }

}

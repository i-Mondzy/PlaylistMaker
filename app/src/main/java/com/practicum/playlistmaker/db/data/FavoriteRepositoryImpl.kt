package com.practicum.playlistmaker.db.data

import com.practicum.playlistmaker.db.data.entity.TrackEntity
import com.practicum.playlistmaker.db.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDataBase: AppDataBase
) : FavoriteRepository {

    override suspend fun saveTrack(track: Track) {
        val trackEntity = with(track) {
            TrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackEntity = with(track) {
            TrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.trackDao().deleteTrack(trackEntity)
    }

    override fun getTracks(): Flow<List<Track>> {
        return flow {
            val tracks = appDataBase.trackDao().getTracks().map {
                with(it) {
                    Track(
                        trackId.toLong(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl
                    )
                }
            }
            emit(tracks)
        }
    }

}

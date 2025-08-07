package com.practicum.playlistmaker.db.data

import com.practicum.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.db.domain.FavoriteRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(
    private val appDataBase: AppDataBase
) : FavoriteRepository {

    override suspend fun saveTrack(track: Track) {
        val favoriteTrackEntity = with(track) {
            FavoriteTrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.favoriteTrackDao().insertTrack(favoriteTrackEntity)
    }

    override suspend fun deleteTrack(track: Track) {
        val favoriteTrackEntity = with(track) {
            FavoriteTrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.favoriteTrackDao().deleteTrack(favoriteTrackEntity)
    }

    override fun getTracks(): Flow<List<Track>> {
        return flow {
            val tracks = appDataBase.favoriteTrackDao().getTracks().map {
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

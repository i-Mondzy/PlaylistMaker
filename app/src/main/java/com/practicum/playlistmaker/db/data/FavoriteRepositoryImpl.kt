package com.practicum.playlistmaker.db.data

import com.practicum.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.db.domain.FavoriteRepository
import com.practicum.playlistmaker.search.data.local.track.TracksManager
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepositoryImpl(
    private val appDataBase: AppDataBase,
    private val tracksManager: TracksManager
) : FavoriteRepository {

    override suspend fun saveTrack(track: Track) {
        val trackHistory = track.copy( isFavorite = true)
        val favoriteTrackEntity = with(track) {
            FavoriteTrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.favoriteTrackDao().insertTrack(favoriteTrackEntity)
        tracksManager.saveTrack(trackHistory)
    }

    override suspend fun deleteTrack(track: Track) {
        val trackHistory = track.copy( isFavorite = false)
        val favoriteTrackEntity = with(track) {
            FavoriteTrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.favoriteTrackDao().deleteTrack(favoriteTrackEntity)
        tracksManager.saveTrack(trackHistory)
    }

    override suspend fun getTracks(): Flow<List<Track>> {
        val tracks = appDataBase.favoriteTrackDao().getTracks().map { entities ->
            entities.map { trackEntity ->
                with(trackEntity) {
                    Track(
                        trackId.toLong(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl
                    )
                }
            }
        }
        return tracks
    }

}

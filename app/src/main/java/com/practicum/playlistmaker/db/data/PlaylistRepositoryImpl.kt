package com.practicum.playlistmaker.db.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.data.entity.PlaylistEntity
import com.practicum.playlistmaker.db.data.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.db.domain.PlaylistRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val appDataBase: AppDataBase) : PlaylistRepository {
    
    private val gson = Gson()

    override suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = with(playlist) {
            PlaylistEntity(
                playlistId = playlistId,
                namePlaylist = namePlaylist,
                description = description,
                imgPath = imgPath,
                trackList = gson.toJson(playlist.trackList),
                tracksCount = playlist.trackList.size.toLong()
            )
        }
        
        appDataBase.playlistDao().insertPlaylist(playlistEntity)
    }

    override fun getPlaylist(): Flow<List<Playlist>> {
        return flow {
            val playlist = appDataBase.playlistDao().getPlaylists().map { playlistEntity ->
                with(playlistEntity) {
                    Playlist(
                        playlistId = playlistId,
                        namePlaylist = namePlaylist,
                        description = description,
                        imgPath = imgPath,
                        trackList = gson.fromJson(playlistEntity.trackList, object : TypeToken<List<Playlist>>() {}.type),
                        tracksCount = tracksCount
                    )
                }
            }
            emit(playlist)
        }
    }

    override suspend fun saveTrack(playlistTrack: Track) {
        val playlistTrackEntity = with(playlistTrack) {
            PlaylistTrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.playlistTrackDao().insertTrack(playlistTrackEntity)
    }

    override suspend fun deleteTrack(playlistTrack: Track) {
        val playlistTrackEntity = with(playlistTrack) {
            PlaylistTrackEntity(trackId.toString(), trackName, artistName, trackTimeMillis, artworkUrl100, collectionName, releaseDate, primaryGenreName, country, previewUrl)
        }
        appDataBase.playlistTrackDao().deleteTrack(playlistTrackEntity)
    }

}

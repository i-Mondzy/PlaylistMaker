package com.practicum.playlistmaker.db.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.data.entity.PlaylistEntity
import com.practicum.playlistmaker.db.data.entity.PlaylistTrackEntity
import com.practicum.playlistmaker.db.domain.PlaylistRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.any
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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

    override suspend fun deletePlaylist(playlist: Playlist) {

        withContext(Dispatchers.IO) {
            appDataBase.playlistDao().deletePlaylist(playlist.playlistId)
            for (trackId in playlist.trackList) {
                deleteTrack(trackId, skipPlaylistId = playlist.playlistId)
            }
        }

    }

    override suspend fun updatePlaylist(playlist: Playlist) {
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

        appDataBase.playlistDao().updatePlaylist(playlistEntity)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        val playlists = appDataBase.playlistDao().getPlaylists().map { entities ->
            entities.map { playlistEntity ->
                with(playlistEntity) {
                    Playlist(
                        playlistId = playlistId,
                        namePlaylist = namePlaylist,
                        description = description,
                        imgPath = imgPath,
                        trackList = gson.fromJson(
                            playlistEntity.trackList,
                            object : TypeToken<List<Long>>() {}.type
                        ),
                        tracksCount = tracksCount
                    )
                }
            }
        }

        return playlists
    }

    override fun getPlaylist(playlistId: Long): Flow<Playlist> {

        return flow {
            val playlist = with(appDataBase.playlistDao().getPlaylistId(playlistId)) {
                Playlist(
                    playlistId = playlistId,
                    namePlaylist = namePlaylist,
                    description = description,
                    imgPath = imgPath,
                    trackList = gson.fromJson(trackList, object : TypeToken<List<Long>>() {}.type),
                    tracksCount = tracksCount
                )
            }
            emit(playlist)
        }
    }


    override suspend fun saveTrack(playlistTrack: Track) {
        val playlistTrackEntity = with(playlistTrack) {
            PlaylistTrackEntity(
                trackId.toString(),
                trackName,
                artistName,
                trackTimeMillis,
                artworkUrl100,
                collectionName,
                releaseDate,
                primaryGenreName,
                country,
                previewUrl
            )
        }
        appDataBase.playlistTrackDao().insertTrack(playlistTrackEntity)
    }

    override fun getTracks(tracks: List<Long>): Flow<List<Track>> {
        return flow {
            val favoritesTracksId = appDataBase.favoriteTrackDao().getTracksId().map { it.toLong() }
            val allTracks = appDataBase.playlistTrackDao().getTracks().map { trackEntity ->
                with(trackEntity) {
                    Track(
                        trackId = trackId.toLong(),
                        trackName = trackName,
                        artistName = artistName,
                        trackTimeMillis = trackTimeMillis,
                        artworkUrl100 = artworkUrl100,
                        collectionName = collectionName,
                        releaseDate = releaseDate,
                        primaryGenreName = primaryGenreName,
                        country = country,
                        previewUrl = previewUrl,
                        isFavorite = trackId.toLong() in favoritesTracksId
                    )
                }
            }
            val tracksInPlaylist = allTracks.filter { track -> track.trackId in tracks }

            emit(tracksInPlaylist)
        }
    }

    override suspend fun deleteTrack(trackId: Long, skipPlaylistId: Long?) {
        if (!checkTrackInPlaylists(trackId, skipPlaylistId)) {
            appDataBase.playlistTrackDao().deleteTrack(trackId)
        }
    }

    private suspend fun checkTrackInPlaylists(
        trackId: Long,
        skipPlaylistId: Long? = null
    ): Boolean {
        val playlists = appDataBase.playlistDao().getPlaylists().map { entities ->
            entities
                .filter { it.playlistId != skipPlaylistId }
                .map { playlistEntity ->
                    with(playlistEntity) {
                        Playlist(
                            playlistId = playlistId,
                            namePlaylist = namePlaylist,
                            description = description,
                            imgPath = imgPath,
                            trackList = gson.fromJson(
                                playlistEntity.trackList,
                                object : TypeToken<List<Long>>() {}.type
                            ),
                            tracksCount = tracksCount
                        )
                    }
                }
        }

        return playlists.any { playlists ->
            playlists.any { playlist ->
                playlist.trackList.contains(trackId)
            }
        }
    }

    override suspend fun clearTable() {
        appDataBase.playlistDao().clearTable()
    }

}

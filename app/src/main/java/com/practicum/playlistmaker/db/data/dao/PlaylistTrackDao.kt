package com.practicum.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.db.data.entity.PlaylistTrackEntity
@Dao
interface PlaylistTrackDao {

    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Delete(entity = PlaylistTrackEntity::class)
    suspend fun deleteTrack(track: PlaylistTrackEntity)

    @Query("SELECT COUNT(*) FROM playlist_track_table")
    suspend fun getTracksCount(): Long

}

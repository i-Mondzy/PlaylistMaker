package com.practicum.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.db.data.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {

    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete(entity = FavoriteTrackEntity::class)
    suspend fun deleteTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM track_table")
    fun getTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT trackId FROM track_table")
    fun getTracksId(): Flow<List<String>>

}

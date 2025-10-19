package com.practicum.playlistmaker.db.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.db.data.dao.PlaylistDao
import com.practicum.playlistmaker.db.data.dao.FavoriteTrackDao
import com.practicum.playlistmaker.db.data.dao.PlaylistTrackDao
import com.practicum.playlistmaker.db.data.entity.PlaylistEntity
import com.practicum.playlistmaker.db.data.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.db.data.entity.PlaylistTrackEntity

@Database(version = 6, entities = [FavoriteTrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class])
abstract class AppDataBase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun playlistTrackDao(): PlaylistTrackDao

}

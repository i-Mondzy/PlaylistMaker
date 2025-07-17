package com.practicum.playlistmaker.db.di

import androidx.room.Room
import com.practicum.playlistmaker.db.data.AppDataBase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataBaseDataModule = module {

    single {
        Room.databaseBuilder(androidContext(), klass = AppDataBase::class.java, name = "database.db").build()
    }

}

package com.practicum.playlistmaker.db.di

import com.practicum.playlistmaker.db.data.FavoriteRepositoryImpl
import com.practicum.playlistmaker.db.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.db.domain.FavoriteRepository
import com.practicum.playlistmaker.db.domain.PlaylistRepository
import org.koin.dsl.module

val dataBaseRepositoryModule = module {

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }

}

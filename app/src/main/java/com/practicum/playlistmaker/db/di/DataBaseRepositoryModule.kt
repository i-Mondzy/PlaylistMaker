package com.practicum.playlistmaker.db.di

import com.practicum.playlistmaker.db.data.FavoriteRepositoryImpl
import com.practicum.playlistmaker.db.domain.FavoriteRepository
import org.koin.dsl.module

val dataBaseRepositoryModule = module {

    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get())
    }

}

package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

class RepositoryModule {

    val repositoryModule = module {
        single<TracksRepository> {
            TracksRepositoryImpl(get(), get())
        }
    }

}
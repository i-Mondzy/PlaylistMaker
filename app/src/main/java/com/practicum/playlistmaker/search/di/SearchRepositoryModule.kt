package com.practicum.playlistmaker.search.di

import android.util.Log
import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val searchRepositoryModule = module {

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

}

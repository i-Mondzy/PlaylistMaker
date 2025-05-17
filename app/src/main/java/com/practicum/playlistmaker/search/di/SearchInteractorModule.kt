package com.practicum.playlistmaker.search.di

import android.util.Log
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val searchInteractorModule = module {

    single<Executor> {
        Executors.newCachedThreadPool()
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

}

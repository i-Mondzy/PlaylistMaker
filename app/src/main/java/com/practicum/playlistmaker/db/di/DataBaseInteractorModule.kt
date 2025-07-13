package com.practicum.playlistmaker.db.di

import com.practicum.playlistmaker.db.domain.FavoriteInteractor
import com.practicum.playlistmaker.db.domain.impl.FavoriteInteractorImpl
import org.koin.dsl.module

val dataBaseInteractorModule = module {

    single<FavoriteInteractor> {
        FavoriteInteractorImpl(get())
    }

}

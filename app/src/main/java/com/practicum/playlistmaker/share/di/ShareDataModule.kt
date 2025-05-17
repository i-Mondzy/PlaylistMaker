package com.practicum.playlistmaker.share.di

import com.practicum.playlistmaker.share.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val shareDataModule = module {

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

}

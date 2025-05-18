package com.practicum.playlistmaker.settings.di

import com.practicum.playlistmaker.settings.data.theme.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.dsl.module

val settingsRepositoryModule = module {

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

}

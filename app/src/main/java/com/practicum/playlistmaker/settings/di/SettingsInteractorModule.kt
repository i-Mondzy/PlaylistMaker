package com.practicum.playlistmaker.settings.di

import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val settingsInteractorModule = module {
    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}
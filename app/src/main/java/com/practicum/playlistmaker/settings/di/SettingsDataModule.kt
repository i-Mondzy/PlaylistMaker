package com.practicum.playlistmaker.settings.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.theme.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsDataModule = module {
    single(named("theme_prefs")) {
        androidContext().getSharedPreferences("theme", Context.MODE_PRIVATE)
    }

    single {
        ThemeManager(get(named("theme_prefs")))
    }
}

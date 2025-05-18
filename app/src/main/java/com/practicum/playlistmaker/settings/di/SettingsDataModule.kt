package com.practicum.playlistmaker.settings.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.theme.ThemeManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val LIGHT_DARK_THEME = "theme_prefs"

val settingsDataModule = module {
    single(named(LIGHT_DARK_THEME)) {
        androidContext().getSharedPreferences("theme", Context.MODE_PRIVATE)
    }

    single {
        ThemeManager(get(named(LIGHT_DARK_THEME)))
    }
}

package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator


const val LIGHT_DARK_THEME = "theme"
const val THEME_KEY = "key_theme"

class App : Application() {

    private val getThemeInteractor = Creator.provideSettingsInterator(this)

    override fun onCreate() {
        super.onCreate()

        getThemeInteractor.switchTheme(getThemeInteractor.getTheme())

    }
}
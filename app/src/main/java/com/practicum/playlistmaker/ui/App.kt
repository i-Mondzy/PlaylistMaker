package com.practicum.playlistmaker.ui

import android.app.Application
import com.practicum.playlistmaker.creator.Creator


const val LIGHT_DARK_THEME = "theme"
const val THEME_KEY = "key_theme"

class App : Application() {

    private val getThemeInteractor = Creator.provideThemeInterator(this)

    override fun onCreate() {
        super.onCreate()

        getThemeInteractor.switchTheme(getThemeInteractor.getTheme())

    }
}
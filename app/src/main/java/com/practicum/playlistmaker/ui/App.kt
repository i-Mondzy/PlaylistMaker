package com.practicum.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator


const val LIGHT_DARK_THEME = "theme"
const val THEME_KEY = "key_theme"

class App : Application() {

    private val getThemeInteractor = Creator.provideThemeInterator(this)

    override fun onCreate() {
        super.onCreate()

        switchTheme(getThemeInteractor.getTheme())

    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
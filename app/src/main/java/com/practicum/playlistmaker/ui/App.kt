package com.practicum.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


const val LIGHT_DARK_THEME = "theme"
const val THEME_KEY = "key_theme"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)

        AppCompatDelegate.setDefaultNightMode(
            if (sharedPrefs.getBoolean(THEME_KEY, darkTheme)) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPrefs = getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)
        sharedPrefs
            .edit()
            .putBoolean(THEME_KEY, darkTheme)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
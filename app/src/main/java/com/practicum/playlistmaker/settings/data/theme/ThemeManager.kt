package com.practicum.playlistmaker.settings.data.theme

import android.content.SharedPreferences
import android.util.Log

class ThemeManager(private val sharedPrefs: SharedPreferences) {

    companion object {
        const val THEME_KEY = "key_theme"
    }

    fun saveTheme(darkThemeEnabled: Boolean) {

        sharedPrefs
            .edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()

    }

    fun getTheme(): Boolean {

        return sharedPrefs.getBoolean(THEME_KEY, false)

    }

}

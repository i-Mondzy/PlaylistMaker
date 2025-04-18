package com.practicum.playlistmaker.data.local.theme

import android.app.Application.MODE_PRIVATE
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {

    companion object {
        const val LIGHT_DARK_THEME = "theme"
        const val THEME_KEY = "key_theme"
    }

    fun saveTheme(darkThemeEnabled: Boolean) {

        val sharedPrefs = context.getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)
        sharedPrefs
            .edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()
        Log.d("saveTheme", "${getTheme()}")

    }

    fun getTheme(): Boolean {

        val sharedPrefs = context.getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)

        return sharedPrefs.getBoolean(THEME_KEY, false)

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

package com.practicum.playlistmaker.settings.data.theme

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(private val themeManager: ThemeManager) : SettingsRepository {

    override fun saveTheme(darkThemeEnabled: Boolean) {
        themeManager.saveTheme(darkThemeEnabled)
    }

    override fun getTheme(): Boolean {
        return themeManager.getTheme()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}
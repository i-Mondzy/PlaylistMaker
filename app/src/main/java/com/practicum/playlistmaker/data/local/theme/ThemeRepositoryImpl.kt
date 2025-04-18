package com.practicum.playlistmaker.data.local.theme

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.theme.ThemeRepository

class ThemeRepositoryImpl(private val themeManager: ThemeManager) : ThemeRepository {

    override fun saveTheme(darkThemeEnabled: Boolean) {
        themeManager.saveTheme(darkThemeEnabled)
    }

    override fun getTheme(): Boolean {
        return themeManager.getTheme()
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
        themeManager.switchTheme(darkThemeEnabled)
    }

}
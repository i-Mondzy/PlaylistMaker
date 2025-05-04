package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {

    fun saveTheme(darkThemeEnabled: Boolean)

    fun getTheme(): Boolean

    fun switchTheme(darkThemeEnabled: Boolean)

}
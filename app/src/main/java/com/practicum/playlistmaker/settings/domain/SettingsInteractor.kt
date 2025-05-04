package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {

    fun saveTheme(darkThemeEnabled: Boolean)

    fun getTheme(): Boolean

    fun switchTheme(darkThemeEnabled: Boolean)

}
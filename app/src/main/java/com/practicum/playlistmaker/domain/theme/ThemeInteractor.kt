package com.practicum.playlistmaker.domain.theme

interface ThemeInteractor {

    fun saveTheme(darkThemeEnabled: Boolean)

    fun getTheme(): Boolean

}
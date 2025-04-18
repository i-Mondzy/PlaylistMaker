package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.theme.ThemeInteractor
import com.practicum.playlistmaker.domain.theme.ThemeRepository

class ThemeInteractorImpl(private val repository: ThemeRepository) : ThemeInteractor {

    override fun saveTheme(darkThemeEnabled: Boolean) {
        repository.saveTheme(darkThemeEnabled)
    }

    override fun getTheme(): Boolean {
        return repository.getTheme()
    }

}
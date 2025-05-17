package com.practicum.playlistmaker.creator

import android.app.Application
import com.practicum.playlistmaker.settings.data.theme.ThemeManager
import com.practicum.playlistmaker.settings.data.theme.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.share.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.ShareInteractor
import com.practicum.playlistmaker.share.domain.impl.ShareInteractorImpl

object Creator {

    private lateinit var context: Application

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(ThemeManager(context))
    }

    fun provideSettingsInterator(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideShareInteractor(): ShareInteractor {
        return ShareInteractorImpl(getExternalNavigator())
    }

}

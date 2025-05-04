package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.settings.data.theme.ThemeManager
import com.practicum.playlistmaker.settings.data.theme.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.local.track.TracksManager
import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.TracksRetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.share.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.ShareInteractor
import com.practicum.playlistmaker.share.domain.impl.ShareInteractorImpl

object Creator {
    /*private fun getTracksManager(context: Context) : TracksManager {
        return TracksManager(context)
    }*/

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(TracksRetrofitNetworkClient(), TracksManager(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSettingsRepository(context: Context) : SettingsRepository {
        return SettingsRepositoryImpl(ThemeManager(context))
    }

    fun provideSettingsInterator(context: Context) : SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }

    private fun getExternalNavigator(context: Context) : ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideShareInteractor(context: Context) : ShareInteractor {
        return ShareInteractorImpl(getExternalNavigator(context))
    }

}
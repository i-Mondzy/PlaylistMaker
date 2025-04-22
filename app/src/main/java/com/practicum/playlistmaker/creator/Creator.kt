package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.local.theme.ThemeManager
import com.practicum.playlistmaker.data.local.theme.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.local.track.TracksManager
import com.practicum.playlistmaker.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.TracksRetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.theme.ThemeInteractor
import com.practicum.playlistmaker.domain.theme.ThemeRepository

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

    private fun getThemeRepository(context: Context) : ThemeRepository {
        return ThemeRepositoryImpl(ThemeManager(context))
    }

    fun provideThemeInterator(context: Context) : ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

}
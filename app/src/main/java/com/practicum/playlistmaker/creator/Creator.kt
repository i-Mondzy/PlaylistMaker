package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.data.local.TracksManager
import com.practicum.playlistmaker.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.TracksRetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksManager(context: Context) : TracksManager {
        return TracksManager(context)
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(TracksRetrofitNetworkClient(), getTracksManager(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }
}
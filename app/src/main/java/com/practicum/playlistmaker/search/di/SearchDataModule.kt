package com.practicum.playlistmaker.search.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.TracksNetworkClient
import com.practicum.playlistmaker.search.data.local.track.TracksManager
import com.practicum.playlistmaker.search.data.network.TrackApi
import com.practicum.playlistmaker.search.data.network.TracksRetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TRACKS_HISTORY = "history_prefs"

val searchDataModule = module {

    // TracksManager
    single(named(TRACKS_HISTORY)) {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single {
        Gson()
    }

    single {
        TracksManager(get(named(TRACKS_HISTORY)), get())
    }


    // TracksRetrofitNetworkClient
    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)
    }

    single<TracksNetworkClient> {
        TracksRetrofitNetworkClient(get())
    }

}

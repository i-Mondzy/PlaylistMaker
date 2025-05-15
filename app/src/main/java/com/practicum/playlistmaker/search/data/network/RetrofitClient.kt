package com.practicum.playlistmaker.search.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val client: Retrofit by lazy {
        Retrofit.Builder().baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api: TrackApi by lazy {
        client.create(TrackApi::class.java)
    }

}
package com.practicum.playlistmaker.search.data.network

import android.util.Log
import com.practicum.playlistmaker.search.data.dto.NetworkResponse
import com.practicum.playlistmaker.search.data.TracksNetworkClient


class TracksRetrofitNetworkClient(private val trackApi: TrackApi) : TracksNetworkClient {

    override fun doRequest(text: String): NetworkResponse {
        return try {
            val response = trackApi.search(text).execute()
            val networkResponse = response.body() ?: NetworkResponse()

            Log.d("resultCode", "${response.code()}")
            networkResponse.apply { resultCode = response.code() }
        } catch (ex: Exception) {
            Log.d("resultCode", "$ex")
            NetworkResponse().apply { resultCode = 400 }
        }
    }

}
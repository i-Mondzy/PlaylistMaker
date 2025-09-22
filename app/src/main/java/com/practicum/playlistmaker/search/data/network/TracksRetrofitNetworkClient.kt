package com.practicum.playlistmaker.search.data.network

import android.util.Log
import com.practicum.playlistmaker.search.data.dto.NetworkResponse
import com.practicum.playlistmaker.search.data.TracksNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TracksRetrofitNetworkClient(private val trackApi: TrackApi) : TracksNetworkClient {

    override suspend fun doRequest(text: String): NetworkResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = trackApi.search(text)
                response.apply { resultCode = 200 }
            } catch (ex: Exception) {
                NetworkResponse().apply { resultCode = 500 }
            }
        }
    }

}

package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.NetworkResponse
import com.practicum.playlistmaker.data.TracksNetworkClient


class TracksRetrofitNetworkClient : TracksNetworkClient {

    override fun doRequest(text: String): NetworkResponse {
        return try {
            val response = RetrofitClient.api.search(text).execute()
            val networkResponse = response.body() ?: NetworkResponse()

            networkResponse.apply { resultCode = response.code() }
        } catch (ex: Exception) {
            NetworkResponse().apply { resultCode = 400 }
        }
    }

}
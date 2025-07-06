package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.NetworkResponse

interface TracksNetworkClient {
    suspend fun doRequest(text: String): NetworkResponse
}

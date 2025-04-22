package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.NetworkResponse

interface TracksNetworkClient {
    fun doRequest(text: String): NetworkResponse
}
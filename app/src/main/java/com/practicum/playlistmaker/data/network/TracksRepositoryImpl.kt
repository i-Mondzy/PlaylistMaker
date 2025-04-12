package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.TracksNetworkClient
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.model.Track

class TracksRepositoryImpl(private val networkClient: TracksNetworkClient) : TracksRepository {
    
    override fun searchTracks(text: String): List<Track> {
        val response = networkClient.doRequest(text)

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            emptyList()
        }
    }
    
}
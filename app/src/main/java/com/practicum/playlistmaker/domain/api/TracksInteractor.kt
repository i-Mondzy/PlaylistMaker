package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.Resource
import com.practicum.playlistmaker.domain.model.Track

interface TracksInteractor {
    fun searchTracks(text: String, consumer: TracksConsumer)

    fun saveTrack(track: Track)

    fun getTracks() : List<Track>

    fun clearTracks()

    interface TracksConsumer {
        fun consume(foundTracks: Resource<List<Track>>)
    }
}
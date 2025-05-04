package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTracks(text: String): Resource<List<Track>>

    fun saveTrack(track: Track)

    fun getTracks() : List<Track>

    fun clearTracks()
}
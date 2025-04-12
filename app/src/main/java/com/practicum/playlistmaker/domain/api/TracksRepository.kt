package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.model.Track

interface TracksRepository {
    fun searchTracks(text: String): List<Track>
}
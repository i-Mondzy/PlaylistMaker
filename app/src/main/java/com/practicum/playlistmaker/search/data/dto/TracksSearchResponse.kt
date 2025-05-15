package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.search.domain.model.Track

class TracksSearchResponse(
    val results: ArrayList<TrackDto>
) : NetworkResponse()

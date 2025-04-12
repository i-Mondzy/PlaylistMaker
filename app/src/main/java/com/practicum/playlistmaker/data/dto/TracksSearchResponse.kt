package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.model.Track

class TracksSearchResponse(
    val results: ArrayList<TrackDto>
) : NetworkResponse()

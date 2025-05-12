package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.player.ui.model.TrackUi

data class CombinedData(
    val track: TrackUi?,
    val currentTrackTime: String?,
    val isPlaying: Boolean?,
    val isPlayButtonEnabled: Boolean?
)

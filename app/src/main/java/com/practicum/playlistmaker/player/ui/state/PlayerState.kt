package com.practicum.playlistmaker.player.ui.state

import com.practicum.playlistmaker.player.ui.model.TrackUi

sealed interface PlayerState {

    data class Play(val currentTime: String) : PlayerState
    data object Pause : PlayerState
    data object Stop : PlayerState

}
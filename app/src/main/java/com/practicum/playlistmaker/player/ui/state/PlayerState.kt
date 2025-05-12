package com.practicum.playlistmaker.player.ui.state

import com.practicum.playlistmaker.player.ui.model.TrackUi

sealed interface PlayerState {

    data class Content(val track: TrackUi?) : PlayerState
    data class Play(val time: String) : PlayerState
    data object Pause : PlayerState
    data class Stop(val time: String) : PlayerState

}
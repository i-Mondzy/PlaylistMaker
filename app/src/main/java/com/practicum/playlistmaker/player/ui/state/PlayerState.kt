package com.practicum.playlistmaker.player.ui.state

sealed class PlayerState(val isPlaying: Boolean, val time: String? = null) {

    class Play(time: String) : PlayerState(isPlaying = true, time = time)
    class Pause(time: String) : PlayerState(isPlaying = false, time = time)

}
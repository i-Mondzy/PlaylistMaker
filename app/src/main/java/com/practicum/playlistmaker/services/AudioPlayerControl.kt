package com.practicum.playlistmaker.services

import com.practicum.playlistmaker.player.ui.state.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {

    fun getCurrentState(): StateFlow<PlayerState>
    fun startPlayer()
    fun pausePlayer()

    fun showNotification()

    fun hideNotification()

}

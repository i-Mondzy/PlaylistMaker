package com.practicum.playlistmaker.main.ui.state

sealed interface NavigationState {
    object Search : NavigationState
    object Media : NavigationState
    object Settings : NavigationState
}
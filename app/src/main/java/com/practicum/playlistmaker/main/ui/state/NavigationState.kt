package com.practicum.playlistmaker.main.ui.state

sealed interface NavigationState {
    data object Search : NavigationState
    data object Media : NavigationState
    data object Settings : NavigationState
}
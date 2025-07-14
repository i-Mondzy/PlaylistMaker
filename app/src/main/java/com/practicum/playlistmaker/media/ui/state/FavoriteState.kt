package com.practicum.playlistmaker.media.ui.state

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface FavoriteState {

    data object Empty : FavoriteState
    data class Content(val tracks: List<Track>) : FavoriteState

}

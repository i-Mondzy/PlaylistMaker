package com.practicum.playlistmaker.playlist.ui.state

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi

sealed interface PlaylistState {

    data object Empty : PlaylistState

    data class Content(val playlistUi: PlaylistUi?): PlaylistState

}

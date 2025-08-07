package com.practicum.playlistmaker.media.ui.state

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist

sealed interface PlaylistsState {

    data object Empty: PlaylistsState
    data class Content(val playlists: List<Playlist>): PlaylistsState

}

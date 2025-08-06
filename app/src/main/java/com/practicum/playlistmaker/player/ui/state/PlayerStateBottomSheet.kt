package com.practicum.playlistmaker.player.ui.state

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist

sealed interface PlayerStateBottomSheet {

    data class Content (val playlists: List<Playlist>) : PlayerStateBottomSheet

}

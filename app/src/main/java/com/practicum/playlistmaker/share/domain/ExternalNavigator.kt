package com.practicum.playlistmaker.share.domain

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.share.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)

    fun openLink(link: String)

    fun openEmail(email: EmailData)

    fun sharePlaylist(playlist: Playlist, trackList: List<Track>)

}

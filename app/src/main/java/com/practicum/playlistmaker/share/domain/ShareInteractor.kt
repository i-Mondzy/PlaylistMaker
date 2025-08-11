package com.practicum.playlistmaker.share.domain

import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track


interface ShareInteractor {

    fun shareApp(link: String)

    fun openTerms(link: String)

    fun openSupport(email: String, subject: String, text: String)

    fun sharePlaylist(playlist: Playlist, trackList: List<Track>)

}

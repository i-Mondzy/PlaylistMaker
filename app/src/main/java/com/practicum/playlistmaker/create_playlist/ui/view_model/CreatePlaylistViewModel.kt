package com.practicum.playlistmaker.create_playlist.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    fun savePlaylist(imgPath: String?, name: String, description: String) {
        val playlist = Playlist(
            playlistId = 0L,
            namePlaylist = name,
            description = description,
            imgPath = imgPath.orEmpty(),
            trackList = mutableListOf(),
            tracksCount = 0L
        )

        viewModelScope.launch {
            interactor.savePlaylist(playlist)
        }
    }

}

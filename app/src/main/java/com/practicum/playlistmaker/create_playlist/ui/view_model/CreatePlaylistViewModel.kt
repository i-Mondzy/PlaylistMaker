package com.practicum.playlistmaker.create_playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi
import com.practicum.playlistmaker.playlist.ui.state.PlaylistState
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(private val interactor: PlaylistInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

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

    fun editPlaylist(imgPath: String?, name: String, description: String, playlistId: Long, tracks: List<Long>) {
        val playlist = Playlist(
            playlistId = playlistId,
            namePlaylist = name,
            description = description,
            imgPath = imgPath.orEmpty(),
            trackList = tracks,
            tracksCount = tracks.size.toLong()
        )

        viewModelScope.launch {
            interactor.updatePlaylist(playlist)
        }
    }

    fun setUi(playlistUi: PlaylistUi) {
        stateLiveData.postValue(PlaylistState.Content(playlistUi))
    }

}

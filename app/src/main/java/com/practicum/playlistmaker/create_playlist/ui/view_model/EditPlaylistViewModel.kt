package com.practicum.playlistmaker.create_playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi
import com.practicum.playlistmaker.playlist.ui.state.PlaylistState
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val interactor: PlaylistInteractor) : CreatePlaylistViewModel(interactor) {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    fun setUi(playlistUi: PlaylistUi) {
        stateLiveData.postValue(PlaylistState.Content(playlistUi))
    }
    
    fun editPlaylist(playlistUi: PlaylistUi) {
        viewModelScope.launch { 
            val playlist = with(playlistUi) {
                Playlist(
                    playlistId = playlistId,
                    namePlaylist = namePlaylist,
                    description = description,
                    imgPath = imgPath,
                    trackList = trackList.map { it.trackId },
                    tracksCount = tracksCount.toLong()
                )
            }

            interactor.updatePlaylist(playlist)
        }
    }

}

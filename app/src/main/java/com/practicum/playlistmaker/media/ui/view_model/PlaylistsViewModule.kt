package com.practicum.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.media.ui.state.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModule(private val interactor: PlaylistInteractor) : ViewModel() {

    private val playlists = mutableListOf<Playlist>()

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    init {
        showPlaylists()
        Log.d("init", "init")
    }

    private fun showPlaylists() {
        viewModelScope.launch {
            interactor
                .getPlaylist()
                .collect {
                        playlists -> this@PlaylistsViewModule.playlists.addAll(playlists)
                }

            if (playlists.isEmpty()) {
                renderState(PlaylistsState.Empty)
            } else {
                renderState(PlaylistsState.Content(playlists))
            }
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }

}

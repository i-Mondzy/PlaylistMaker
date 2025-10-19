package com.practicum.playlistmaker.playlist.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi
import com.practicum.playlistmaker.playlist.ui.state.PlaylistState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.share.domain.ShareInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val shareInteractor: ShareInteractor
) : ViewModel() {

    private val tracks = mutableListOf<Track>()
    private var playlistUi: PlaylistUi? = null
    private var playlist: Playlist? = null

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    fun setPlaylist(playlistId: Long) {
        if (playlistUi == null) {

            viewModelScope.launch(Dispatchers.IO) {
                interactor
                    .getPlaylist(playlistId)
                    .collect { playlist = it }

                playlistUi = with(playlist!!) {
                    PlaylistUi(
                        playlistId = this.playlistId,
                        namePlaylist = namePlaylist,
                        description = description,
                        imgPath = imgPath,
                        trackList = tracks,
                        tracksTime = 0.toString(),
                        tracksCount = tracksCount.toString()
                    )
                }

                getTracks(playlist!!)
            }

            return
        }

        getTracks(playlist!!)
    }

    fun updatePlaylist() {
        tracks.clear()
        playlistUi = null
        setPlaylist(playlist?.playlistId!!)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            interactor.deletePlaylist(playlist!!)
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracks.removeIf { it.trackId == track.trackId }
            playlistUi?.tracksTime = (tracks.sumOf { it.trackTimeMillis.toLongOrNull() ?: 0L } / 60000).toString()
            playlistUi?.tracksCount = tracks.size.toString()

            if (playlist != null) {
                playlist = with(playlist) {
                    this!!.copy(
                        playlistId = playlistId,
                        namePlaylist = namePlaylist,
                        description = description,
                        imgPath = imgPath,
                        trackList = tracks.map { it.trackId },
                        tracksCount = tracksCount
                    )
                }

                interactor.updatePlaylist(playlist!!)
            }

            interactor.deleteTrack(track.trackId)
            stateLiveData.postValue(PlaylistState.Content(playlistUi))
        }
    }

    fun share() {
        shareInteractor.sharePlaylist(playlist!!, tracks)
    }

    private fun getTracks(playlist: Playlist) {
        viewModelScope.launch(Dispatchers.IO) {
            tracks.clear()
            interactor
                .getTracks(playlist.trackList)
                .collect {
                    tracks -> this@PlaylistViewModel.tracks.addAll(tracks)
                }

            playlistUi?.tracksTime = (tracks.sumOf { it.trackTimeMillis.toLongOrNull() ?: 0L } / 60000).toString()
            stateLiveData.postValue(PlaylistState.Content(playlistUi))
        }
    }

}

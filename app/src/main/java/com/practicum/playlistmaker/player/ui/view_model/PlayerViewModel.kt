package com.practicum.playlistmaker.player.ui.view_model

import android.app.Service
import android.content.ServiceConnection
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.FavoriteInteractor
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import com.practicum.playlistmaker.player.ui.state.PlayerStateBottomSheet
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.services.AudioPlayerControl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PlayerViewModel(
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var audioPlayerControl: AudioPlayerControl? = null
    private val playlistsBS = mutableListOf<Playlist>()
    private var trackUi: TrackUi? = null

    var init = false

    private var service: ServiceConnection? = null
    private var serviceList = mutableListOf<ServiceConnection>()

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun getStateLiveData(): LiveData<PlayerState> = stateLiveData

    private val stateLiveDataBS = MutableLiveData<PlayerStateBottomSheet>()
    fun getStateLiveDataBS(): LiveData<PlayerStateBottomSheet> = stateLiveDataBS

    /*private val mediatorLiveData = MediatorLiveData<PlayerState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                is PlayerState.Content -> PlayerState.Content(trackUi?.copy(
                    currentTime = getCurrentPlayerPosition()
                ))
                is PlayerState.Pause -> state
                is PlayerState.Play -> state
                is PlayerState.Stop -> state
            }
        }
    }*/

    fun setTrack(track: Track) {
        if (trackUi == null) {

            trackUi = TrackUi(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                currentTime = "00:30",
                trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong()),
                artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                collectionName = track.collectionName,
                releaseDate = SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(track.releaseDate)),
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                isFavorite = track.isFavorite
            )

            renderState(PlayerState.Content(trackUi))
            getPlaylists()

            return
        }

        renderState(PlayerState.Content(trackUi))
        getPlaylists()
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (trackUi?.isFavorite == false) {
                    favoriteInteractor.saveTrack(track)
                    trackUi?.isFavorite = true
                } else {
                    favoriteInteractor.deleteTrack(track)
                    trackUi?.isFavorite = false
                }

                stateLiveData.postValue(PlayerState.Content(trackUi))
            }
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            playlistsBS.clear()
            playlistInteractor
                .getPlaylists()
                .collect {
                    playlists -> playlistsBS.addAll(playlists)
                }

            renderStateBS(PlayerStateBottomSheet.Content(playlistsBS))
        }
    }

    fun onPlaylistClicked(track: Track, position: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (!playlistsBS[playlistsBS.indexOfFirst{ it.playlistId == playlistsBS[position].playlistId }].trackList.contains(track.trackId)) {
                    playlistInteractor.saveTrack(track)
                    playlistsBS[position] = with(playlistsBS[position]) {
                        copy(
                            playlistId = playlistId,
                            namePlaylist = namePlaylist,
                            description = description,
                            imgPath = imgPath,
                            trackList = trackList + track.trackId,
                            tracksCount = trackList.size.toLong()
                        )
                    }
                    playlistInteractor.updatePlaylist(playlistsBS[position])
                    getPlaylists()
                }

                stateLiveDataBS.postValue(
                    PlayerStateBottomSheet.Content(
                        playlistsBS
                    )
                )
            }
        }
    }

    fun playbackControl() {
        if (stateLiveData.value is PlayerState.Play) {
            audioPlayerControl?.pausePlayer()
        } else {
            audioPlayerControl?.startPlayer()
        }
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.value = state
    }

    private fun renderStateBS(state: PlayerStateBottomSheet) {
        stateLiveDataBS.value = state
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getCurrentState().collect {
                stateLiveData.postValue(it)
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    fun setService(service: ServiceConnection) {
        this.service = service
        serviceList.add(service)
    }

    fun getServices(): List<ServiceConnection> = serviceList

    fun showNotification() {
        audioPlayerControl?.showNotification()
    }

    fun hideNotification() {
        audioPlayerControl?.hideNotification()
    }

}

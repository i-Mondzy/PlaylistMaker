package com.practicum.playlistmaker.player.ui.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.db.domain.FavoriteInteractor
import com.practicum.playlistmaker.db.domain.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerStateBottomSheet
import com.practicum.playlistmaker.player.ui.state.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayer: MediaPlayer,
    private val favoriteInteractor: FavoriteInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistsBS = mutableListOf<Playlist>()

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun getStateLiveData(): LiveData<PlayerState> = mediatorLiveData

    private val stateLiveDataBS = MutableLiveData<PlayerStateBottomSheet>()
    fun getStateLiveDataBS(): LiveData<PlayerStateBottomSheet> = stateLiveDataBS

    private val mediatorLiveData = MediatorLiveData<PlayerState>().also { liveData ->
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
    }

    private var playerState = STATE_DEFAULT

    private var totalMillis = 30000
    private var trackUi: TrackUi? = null

    private var timerJob: Job? = null

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 3
        private const val STATE_PAUSED = 4
    }

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

            preparePlayer(trackUi!!.previewUrl)

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

                stateLiveData.postValue(
                    PlayerState.Content(
                        trackUi
                    )
                )
            }
        }
    }

    private fun getPlaylists() {
        viewModelScope.launch {
            playlistsBS.clear()
            playlistInteractor
                .getPlaylist()
                .collect {
                    playlists -> playlistsBS.addAll(playlists)
                }

            renderStateBS(PlayerStateBottomSheet.Content(playlistsBS))
            Log.d("getPlaylists", "${playlistsBS.flatMap { it.trackList }}")
        }
    }

    fun onPlaylistClicked(track: Track, position: Int) {
        Log.d("onPlaylistClicked", "click!")
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
                    Log.d("playlist", "${playlistsBS[position].trackList}")
                }

                stateLiveDataBS.postValue(
                    PlayerStateBottomSheet.Content(
                        playlistsBS
                    )
                )
            }
        }
    }

    private fun play() {
        mediaPlayer.start()
        renderState(PlayerState.Play(getCurrentPlayerPosition()))
        playerState = STATE_PLAYING

        startTimer()
    }

    fun pause() {
        mediaPlayer.pause()
        timerJob?.cancel()
        renderState(PlayerState.Pause(getCurrentPlayerPosition()))
        playerState = STATE_PAUSED
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pause()
            }

            STATE_PREPARED -> {
                play()
            }

            STATE_PAUSED -> {
                play()
            }
        }
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            renderState(PlayerState.Stop("00:30"))
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            renderState(PlayerState.Stop("00:00"))
            playerState = STATE_PREPARED
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300)
                stateLiveData.postValue(PlayerState.Play(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(totalMillis - mediaPlayer.currentPosition) ?: "00:00"
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.value = state
    }

    private fun renderStateBS(state: PlayerStateBottomSheet) {
        stateLiveDataBS.value = state
    }

    override fun onCleared() {
        mediaPlayer.pause()
        mediaPlayer.release()
    }

}

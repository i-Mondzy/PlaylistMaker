package com.practicum.playlistmaker.player.ui.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import java.util.Locale

class PlayerViewModel : ViewModel() {

    /*private val trackLiveData = MutableLiveData<TrackUi>()
    fun getTrackLiveData(): LiveData<TrackUi> = trackLiveData

    private val currentTrackTimeLiveData = MutableLiveData<String>()
    fun getCurrentTrackTimeLiveData(): LiveData<String> = currentTrackTimeLiveData

    private val isPlaying = MutableLiveData<Boolean>()
    fun getIsPlaying(): LiveData<Boolean> = isPlaying

    private val isPlayButtonEnabled = MutableLiveData<Boolean>()
    fun getIsPlayButtonEnabled(): LiveData<Boolean> = isPlayButtonEnabled*/

    private val playerStateUi = MutableLiveData<PlayerState>()
    fun getPlayerStateUi(): LiveData<PlayerState> = playerStateUi


    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())


    private var time = formatTime(30)
    private var seconds = 30
    private var pauseSeconds = 30
    private var startTime = 0L
    private var pauseTime = 0L
    private var remainingMillis = 1000L

    private val playRunnable = object : Runnable {
        override fun run() {
            startTime = System.currentTimeMillis()

            seconds -= 1
            time = formatTime(seconds)

            handler.postDelayed(this, 1000L)
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 3
        private const val STATE_PAUSED = 4

        private const val DEF_TIME = 30L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel()
            }
        }
    }

    fun setTrack(track: Track) {
        val trackUi = TrackUi(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong()),
            artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
            collectionName = track.collectionName,
            releaseDate = SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(track.releaseDate)),
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )

        playerStateUi.value = PlayerState.Content(trackUi, true)

        if (playerState == STATE_PAUSED) {
            playerState = STATE_DEFAULT
        }

        preparePlayer(trackUi.previewUrl)
    }

    private fun play() {
        mediaPlayer.start()
        playerState = STATE_PLAYING

        startTime = System.currentTimeMillis()

        if (seconds == 30) time = formatTime(DEF_TIME.toInt())

        handler.postDelayed(playRunnable, remainingMillis)
    }

    fun pause() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED

        pauseTime = System.currentTimeMillis()

        if (pauseSeconds != seconds) {
            remainingMillis = 1000L - (pauseTime - startTime)
        } else {
            remainingMillis -= (pauseTime - startTime)
        }

        pauseSeconds = seconds
        handler.removeCallbacks(playRunnable)
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                PlayerState.Pause(time)
                pause()
            }

            STATE_PREPARED -> {
                PlayerState.Play(time)
                play()
            }

            STATE_PAUSED -> {
                PlayerState.Play(time)
                play()
            }
        }
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        time = formatTime(DEF_TIME.toInt())
//        isPlaying.value = false

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            time = formatTime(seconds)
            if (seconds == 1) time = formatTime(0)
            remainingMillis = 1000L
            seconds = 30
            handler.removeCallbacks(playRunnable)
            playerState = STATE_PREPARED
        }
    }

    private fun formatTime(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds)
    }

    override fun onCleared() {
        mediaPlayer.pause()
        mediaPlayer.release()
    }

}
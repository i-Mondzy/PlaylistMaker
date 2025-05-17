package com.practicum.playlistmaker.player.ui.view_model

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import java.util.Locale

class PlayerViewModel : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerState>()
    fun getStateLiveData(): LiveData<PlayerState> = mediatorLiveData

    private val mediatorLiveData = MediatorLiveData<PlayerState>().also { livedata ->
        livedata.addSource(stateLiveData) { state ->
            livedata.value = when (state) {
                is PlayerState.Content -> PlayerState.Content(trackUi?.copy(currentTime = formatTime(seconds)))
                PlayerState.Pause -> state
                is PlayerState.Play -> state
                is PlayerState.Stop -> state
            }
        }
    }

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private var seconds = 30
    private var pauseSeconds = 30
    private var startTime = 0L
    private var pauseTime = 0L
    private var remainingMillis = 1000L
    private var trackUi: TrackUi? = null

    private val playRunnable = object : Runnable {
        override fun run() {
            startTime = System.currentTimeMillis()

            seconds -= 1
            renderState(PlayerState.Play(formatTime(seconds)))

            handler.postDelayed(this, 1000L)
        }
    }

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
                currentTime = formatTime(seconds),
                trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong()),
                artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                collectionName = track.collectionName,
                releaseDate = SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(track.releaseDate)),
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl
            )

            renderState(PlayerState.Content(trackUi))

            preparePlayer(trackUi!!.previewUrl)

            return
        }

        renderState(PlayerState.Content(trackUi))
    }

    private fun play() {
        mediaPlayer.start()
        renderState(PlayerState.Play(formatTime(seconds)))
        playerState = STATE_PLAYING

        startTime = System.currentTimeMillis()

        handler.postDelayed(playRunnable, remainingMillis)
    }

    fun pause() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer.pause()
            renderState(PlayerState.Pause)
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
            renderState(PlayerState.Stop(formatTime(seconds)))
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            renderState(PlayerState.Stop(formatTime(seconds)))
            if (seconds == 1) renderState(PlayerState.Stop(formatTime(0)))
            remainingMillis = 1000L
            seconds = 30
            handler.removeCallbacks(playRunnable)
            playerState = STATE_PREPARED
        }
    }

    private fun formatTime(seconds: Int): String {
        return String.format("%02d:%02d", seconds / 60, seconds)
    }

    private fun renderState(state: PlayerState) {
        stateLiveData.value = state
    }

    override fun onCleared() {
        mediaPlayer.pause()
        mediaPlayer.release()
    }

}
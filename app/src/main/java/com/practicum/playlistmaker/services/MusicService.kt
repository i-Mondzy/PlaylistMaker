package com.practicum.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class MusicService : Service(), AudioPlayerControl {

//    region Биндер
    private val binder = MusicServiceBinder()
//    endregion

//    region Работа с плеером
    private var mediaPlayer: MediaPlayer? = null
    private var trackUi: TrackUi? = null
    private var totalMillis = 30000
    private var timerJob: Job? = null
//    endregion

//    region State
    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Stop("00:00"))
    val playerState = _playerState.asStateFlow()
//    endregion

    fun showNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )
    }

    fun hideNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music Service",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Service for playing music" }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("${trackUi?.artistName} - ${trackUi?.trackName}")
            .setSmallIcon(R.mipmap.ic_launcher_custom)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun initMediaPlayer() {
        if (trackUi == null) return

        mediaPlayer?.let { mediaPlayer ->
            mediaPlayer.reset()
            mediaPlayer.setDataSource(trackUi?.previewUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                _playerState.value = PlayerState.Stop("00:30")
            }
            mediaPlayer.setOnCompletionListener {
                _playerState.value = PlayerState.Stop("00:00")
            }
        }
    }

    private fun releasePlayer() {
        timerJob?.cancel()

        mediaPlayer?.let { mediaPlayer ->
            mediaPlayer.stop()
            mediaPlayer.setOnPreparedListener(null)
            mediaPlayer.setOnCompletionListener(null)
            mediaPlayer.release()
        }

        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(200)
                _playerState.value = (PlayerState.Play(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(totalMillis - (mediaPlayer?.currentPosition ?: 0)) ?: "00:00"
    }

    override fun onBind(intent: Intent?): IBinder? {
        trackUi = intent?.getParcelableExtra(ARGS_TRACK)

        initMediaPlayer()

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        mediaPlayer = MediaPlayer()
    }

    override fun getCurrentState(): StateFlow<PlayerState> {
        return playerState
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerState.Play(getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerState.Pause(getCurrentPlayerPosition())
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    companion object {
        private const val ARGS_TRACK = "Track"
        const val SERVICE_NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }
}

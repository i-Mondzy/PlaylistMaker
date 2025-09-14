package com.practicum.playlistmaker.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    private val binder = MusicServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }
}

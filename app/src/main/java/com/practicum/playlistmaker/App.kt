package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    private val getThemeInteractor = Creator.provideSettingsInterator(this)

    override fun onCreate() {
        super.onCreate()

        getThemeInteractor.switchTheme(getThemeInteractor.getTheme())

    }
}
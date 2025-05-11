package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {

    lateinit var creator: Creator
    private lateinit var getThemeInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        creator = Creator(applicationContext)
        getThemeInteractor = creator.provideSettingsInterator()

        getThemeInteractor.switchTheme(getThemeInteractor.getTheme())

    }
}
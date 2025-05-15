package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.SettingsInteractor

class App : Application() {

    private lateinit var getThemeInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        Creator.init(this)

        getThemeInteractor = Creator.provideSettingsInterator()

        getThemeInteractor.switchTheme(getThemeInteractor.getTheme())

    }
}
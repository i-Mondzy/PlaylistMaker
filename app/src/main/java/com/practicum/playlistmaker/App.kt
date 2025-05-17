package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.main.di.mainViewModelModule
import com.practicum.playlistmaker.search.di.searchDataModule
import com.practicum.playlistmaker.search.di.searchInteractorModule
import com.practicum.playlistmaker.search.di.searchRepositoryModule
import com.practicum.playlistmaker.search.di.searchViewModelModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private fun switchAppTheme() {
        val settings = getKoin().get<SettingsInteractor>()
        settings.switchTheme(settings.getTheme())
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                searchDataModule, searchRepositoryModule, searchInteractorModule, searchViewModelModule,
                mainViewModelModule
            )
        }

        switchAppTheme()

    }

}
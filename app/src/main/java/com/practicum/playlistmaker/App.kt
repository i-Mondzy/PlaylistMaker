package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.db.di.dataBaseDataModule
import com.practicum.playlistmaker.db.di.dataBaseInteractorModule
import com.practicum.playlistmaker.db.di.dataBaseRepositoryModule
import com.practicum.playlistmaker.media.di.mediaViewModelModule
import com.practicum.playlistmaker.player.di.playerViewModelModule
import com.practicum.playlistmaker.search.di.searchDataModule
import com.practicum.playlistmaker.search.di.searchInteractorModule
import com.practicum.playlistmaker.search.di.searchRepositoryModule
import com.practicum.playlistmaker.search.di.searchViewModelModule
import com.practicum.playlistmaker.settings.di.settingsDataModule
import com.practicum.playlistmaker.settings.di.settingsInteractorModule
import com.practicum.playlistmaker.settings.di.settingsRepositoryModule
import com.practicum.playlistmaker.settings.di.settingsViewModuleModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.share.di.shareDataModule
import com.practicum.playlistmaker.share.di.shareInteractorModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    private fun switchAppTheme() {
        val settings = getKoin().get<SettingsInteractor>()
        settings.switchTheme(settings.getTheme())
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                searchDataModule,
                searchRepositoryModule,
                searchInteractorModule,
                searchViewModelModule,
                settingsDataModule,
                settingsRepositoryModule,
                settingsInteractorModule,
                settingsViewModuleModule,
                shareDataModule,
                shareInteractorModule,
                playerViewModelModule,
                mediaViewModelModule,
                dataBaseDataModule,
                dataBaseRepositoryModule,
                dataBaseInteractorModule
            )
        }

        switchAppTheme()

    }

}

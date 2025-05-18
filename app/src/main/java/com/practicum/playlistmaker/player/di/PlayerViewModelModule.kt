package com.practicum.playlistmaker.player.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {

    factory {
        MediaPlayer()
    }

    viewModel {
        PlayerViewModel(get())
    }

}

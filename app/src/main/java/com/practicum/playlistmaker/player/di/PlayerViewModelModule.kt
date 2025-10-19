package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerViewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get())
    }

}

package com.practicum.playlistmaker.playlist.di

import com.practicum.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playlistViewModelModule = module {

    viewModel {
        PlaylistViewModel(get(), get())
    }

}

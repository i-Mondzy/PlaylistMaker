package com.practicum.playlistmaker.create_playlist.di

import com.practicum.playlistmaker.create_playlist.ui.view_model.CreatePlaylistViewModel
import com.practicum.playlistmaker.create_playlist.ui.view_model.EditPlaylistViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val createPlaylistViewModelModule = module {

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }

}

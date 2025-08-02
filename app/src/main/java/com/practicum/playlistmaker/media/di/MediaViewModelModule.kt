package com.practicum.playlistmaker.media.di

import com.practicum.playlistmaker.media.ui.view_model.FavoriteViewModule
import com.practicum.playlistmaker.media.ui.view_model.PlaylistsViewModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mediaViewModelModule = module {

    viewModel {
        FavoriteViewModule(get())
    }

    viewModel {
        PlaylistsViewModule(get())
    }

}

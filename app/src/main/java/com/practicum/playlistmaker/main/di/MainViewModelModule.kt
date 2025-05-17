package com.practicum.playlistmaker.main.di

import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainViewModelModule = module {

    viewModel {
        MainViewModel()
    }

}
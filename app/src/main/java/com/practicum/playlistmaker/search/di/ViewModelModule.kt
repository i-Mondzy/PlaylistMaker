package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

}

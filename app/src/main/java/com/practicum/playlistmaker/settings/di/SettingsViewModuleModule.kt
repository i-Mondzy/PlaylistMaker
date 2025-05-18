package com.practicum.playlistmaker.settings.di

import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsViewModuleModule = module {

    viewModel {
        SettingsViewModel(get(), get())
    }

}

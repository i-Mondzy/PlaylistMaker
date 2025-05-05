package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.main.ui.state.NavigationState

class MainViewModel : ViewModel() {

    private val navigationLiveData = MutableLiveData<NavigationState>()
    fun observeNavigation(): LiveData<NavigationState> = navigationLiveData

    fun onSearchClick() {
        navigationLiveData.value = NavigationState.Search
    }

    fun onMediaClick() {
        navigationLiveData.value = NavigationState.Media
    }

    fun onSettingsClick() {
        navigationLiveData.value = NavigationState.Settings
    }

}
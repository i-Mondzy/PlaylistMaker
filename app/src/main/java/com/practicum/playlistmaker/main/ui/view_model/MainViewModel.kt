package com.practicum.playlistmaker.main.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.main.ui.state.NavigationState
import com.practicum.playlistmaker.main.ui.utils.SingleEventLiveData

class MainViewModel : ViewModel() {

    private val navigationLiveData = SingleEventLiveData<NavigationState>()
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
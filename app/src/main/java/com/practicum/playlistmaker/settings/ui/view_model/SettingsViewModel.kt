package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsInteractor = Creator.provideSettingsInterator(getApplication())
    private val shareInteractor = Creator.provideShareInteractor(getApplication())

    private val themeLiveData = MutableLiveData<Boolean>()
    fun observeTheme(): LiveData<Boolean> = themeLiveData

    init {
        themeLiveData.value = settingsInteractor.getTheme()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    fun renderSwitchState(checked: Boolean) {
        themeLiveData.value = checked
        settingsInteractor.saveTheme(checked)
        settingsInteractor.switchTheme(checked)
    }

    fun renderShareLink(link: String) {
        shareInteractor.shareApp(link)
    }

    fun renderUserAgreement(link: String) {
        shareInteractor.openTerms(link)
    }

    fun renderEmailSupport(email: String, subject: String, text: String) {
        shareInteractor.openSupport(email, subject, text)
    }

}
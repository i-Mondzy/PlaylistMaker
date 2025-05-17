package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.main.ui.utils.SingleEventLiveData
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.share.domain.ShareInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val shareInteractor: ShareInteractor
) : ViewModel() {

    private val themeLiveData = SingleEventLiveData<Boolean>()
    fun observeTheme(): LiveData<Boolean> = themeLiveData

    init {
        themeLiveData.value = settingsInteractor.getTheme()
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
package com.practicum.playlistmaker.share.domain.impl

import com.practicum.playlistmaker.share.domain.model.EmailData
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.ShareInteractor

class ShareInteractorImpl(private val externalNavigator: ExternalNavigator) : ShareInteractor {

    override fun shareApp(link: String) {
        externalNavigator.shareLink(link)
    }

    override fun openTerms(link: String) {
        externalNavigator.openLink(link)
    }

    override fun openSupport(email: String, subject: String, text: String) {
        val emailModel = getSupportEmailData(email, subject, text)
        externalNavigator.openEmail(emailModel)
    }

    private fun getSupportEmailData(email: String, subject: String, text: String): EmailData {
        return EmailData(email, subject, text)
    }

}

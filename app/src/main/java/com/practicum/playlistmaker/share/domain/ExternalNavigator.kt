package com.practicum.playlistmaker.share.domain

import com.practicum.playlistmaker.share.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(link: String)

    fun openLink(link: String)

    fun openEmail(email: EmailData)

}

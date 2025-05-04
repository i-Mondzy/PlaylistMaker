package com.practicum.playlistmaker.share.domain



interface ShareInteractor {

    fun shareApp(link: String)

    fun openTerms(link: String)

    fun openSupport(email: String, subject: String, text: String)

}

package com.practicum.playlistmaker.share.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.share.domain.model.EmailData
import com.practicum.playlistmaker.share.domain.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, link)
                    type = "text/plain"
                },
                "Поделиться через"
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    override fun openLink(link: String) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(link)
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    override fun openEmail(email: EmailData) {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
                putExtra(Intent.EXTRA_SUBJECT, email.subject)
                putExtra(Intent.EXTRA_TEXT, email.text)
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

}
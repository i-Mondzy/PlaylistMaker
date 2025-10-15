package com.practicum.playlistmaker.share.data.impl

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.share.domain.ExternalNavigator
import com.practicum.playlistmaker.share.domain.model.EmailData
import java.util.Locale

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

    override fun sharePlaylist(playlist: Playlist, trackList: List<Track>) {

        val message = buildString {
            appendLine(playlist.namePlaylist)                 // Название плейлиста
            if (!playlist.description.isNullOrEmpty()) {
                appendLine(playlist.description)    // Описание (если есть)
            }
            appendLine(word(playlist.tracksCount.toInt()))  // Кол-во треков

            // Проходимся по трекам с индексами
            trackList.forEachIndexed { index, track ->
                // track — объект трека. Предположим, у него есть поля:
                // artistName, trackName, trackDuration (в миллисекундах или секундах)
                val duration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())
                appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
            }
        }

        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                },
                "Поделиться через"
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    private fun word(count: Int): String {
        val lastTwoDigits = count % 100
        val lastDigit = count % 10

        val word = when {
            lastTwoDigits in 11..14 -> "треков"
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            else -> "треков"
        }

        return "$count $word"
    }

}

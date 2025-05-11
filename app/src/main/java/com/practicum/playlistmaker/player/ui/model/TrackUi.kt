package com.practicum.playlistmaker.player.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackUi(
    var trackId: Long, // ID трека
    var trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTimeMillis: String, // Продолжительность трека
    var artworkUrl100: String, // Ссылка на изображение обложки
    var collectionName: String, //Название альбома
    var releaseDate: String, // Год релиза трека
    var primaryGenreName: String, // Жанр трека
    var country: String, // Страна исполнителя
    var previewUrl: String // Отрывок трека
) : Parcelable

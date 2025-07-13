package com.practicum.playlistmaker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Long, // ID трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, //Название альбома
    val releaseDate: String, // Год релиза трека
    val primaryGenreName: String, // Жанр трека
    val country: String, // Страна исполнителя
    val previewUrl: String, // Отрывок трека
    var isFavorite: Boolean = false
) : Parcelable

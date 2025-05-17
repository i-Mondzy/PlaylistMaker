package com.practicum.playlistmaker.search.data.local.track

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.model.Track

class TracksManager(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {

    companion object {
        const val HISTORY_KEY = "key_history"
    }

    fun saveTrack(track: Track) {
        val tracksHistory = getTracks().toMutableList()
        tracksHistory.removeAll{it.trackId == track.trackId}
        tracksHistory.add(0, track)

        if (tracksHistory.size > 10) {
            tracksHistory.removeAt(tracksHistory.lastIndex)
        }

        val json = gson.toJson(tracksHistory)

        sharedPrefs
            .edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    fun getTracks() : List<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, "")
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, object : TypeToken<List<Track>>() {}.type)
        } else {
            emptyList()
        }
    }

    fun clearTracks() {
        sharedPrefs
            .edit()
            .clear()
            .apply()
    }

}
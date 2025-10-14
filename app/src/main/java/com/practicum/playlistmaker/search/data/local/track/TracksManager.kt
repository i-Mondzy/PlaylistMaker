package com.practicum.playlistmaker.search.data.local.track

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class TracksManager(
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) {

    companion object {
        const val HISTORY_KEY = "key_history"
    }

    suspend fun saveTrack(track: Track) {
        val tracksFlow = getTracks().first()
        val tracksHistory = tracksFlow.toMutableList()
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

    fun getTracks() : Flow<List<Track>> {

        return callbackFlow {

            // Начальная инициализация
            val emitCurrent = {
                val json = sharedPrefs.getString(HISTORY_KEY, "")
                val list = if (!json.isNullOrEmpty()) {
                    gson.fromJson<List<Track>>(json, object : TypeToken<List<Track>>() {}.type)
                } else {
                    emptyList()
                }
                trySend(list)
            }

            emitCurrent()

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                if (key == HISTORY_KEY) emitCurrent()
            }

            sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                sharedPrefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }

        /*val json = sharedPrefs.getString(HISTORY_KEY, "")
        return if (!json.isNullOrEmpty()) {
            val tracks = gson.fromJson<List<Track>>(json, object : TypeToken<List<Track>>() {}.type)
            flowOf(tracks)
        } else {
            flowOf(emptyList())
        }*/
    }

    fun clearTracks() {
        sharedPrefs
            .edit()
            .clear()
            .apply()
    }

}

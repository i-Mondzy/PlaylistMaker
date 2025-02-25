package com.practicum.playlistmaker.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.recycler.Track
import com.practicum.playlistmaker.data.recycler.TrackAdapter


const val SEARCH_HISTORY = "search_history"
const val HISTORY_KEY = "key_history"

class SearchHistory(private val context: Context, private val editText: EditText?, private val sharedPrefs: SharedPreferences) {

    val historyTrackList = TrackAdapter { saveTrack(it) }

    fun getHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, "")
        if (!json.isNullOrEmpty()) {
            Log.d("GetHH", "${createTrackFromJson(json)}")
            return createTrackFromJson(json)
        }
        return ArrayList()
    }

    fun saveTrack(track: Track) {
        hideKeyboardAndClearFocus()
        historyTrackList.tracks.removeAll{it.trackId == track.trackId}
        historyTrackList.tracks.add(0, track)
        historyTrackList.notifyDataSetChanged()

        if (historyTrackList.tracks.size > 10) {
            historyTrackList.tracks.removeAt(historyTrackList.tracks.lastIndex)
        }

        sharedPrefs
            .edit()
            .putString(HISTORY_KEY, createJsonFromTrack(historyTrackList.tracks))
            .apply()
        Log.d("Save", historyTrackList.tracks.toString())
    }

    fun clearHistory() {
        historyTrackList.tracks.clear()
        sharedPrefs
            .edit()
            .clear()
            .apply()
        Log.d("Clear", historyTrackList.tracks.toString())
    }

    private fun createJsonFromTrack(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTrackFromJson(json: String): ArrayList<Track> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<Track>>() {}.type)
    }

    private fun hideKeyboardAndClearFocus() {
        editText?.let {
            val inputMethodManager = context.getSystemService(InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }
    }

}
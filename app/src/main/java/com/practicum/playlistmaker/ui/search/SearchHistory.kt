package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.ui.player.PlayerActivity
import java.util.Locale


const val SEARCH_HISTORY = "search_history"
const val HISTORY_KEY = "key_history"

class SearchHistory(private val context: Context, private val editText: EditText?, private var getTracksInteractor: TracksInteractor) {

    private val handler = Handler(Looper.getMainLooper())

    val historyTrackList = TrackAdapter { track ->
        hideKeyboardAndClearFocus()
        context.startActivity(
            Intent(context, PlayerActivity::class.java).apply {
                putExtra("TRACK_ARTWORK", track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                putExtra("TRACK_NAME", track.trackName)
                putExtra("ARTIST_NAME", track.artistName)
                putExtra("TRACK_TIME", SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong()))
                putExtra("COLLECTION_NAME", track.collectionName)
                putExtra("RELEASE_DATE", SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(track.releaseDate)))
                putExtra("PRIMARY_GENRE_NAME", track.primaryGenreName)
                putExtra("COUNTRY", track.country)
                putExtra("PREVIEW", track.previewUrl)

            }
        )

//        Задержка обновления списка истории после открытия Плеера
        handler.postDelayed(
            {
                getTracksInteractor.saveTrack(track)
                updateHistory(getTracksInteractor.getTracks())
            },
            1000L
        )
    }

    /*fun getHistory(): ArrayList<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, "")
        if (!json.isNullOrEmpty()) {
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
    }*/

    /*fun clearHistory() {
        historyTrackList.tracks.clear()
        sharedPrefs
            .edit()
            .clear()
            .apply()
    }*/

    /*private fun createJsonFromTrack(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTrackFromJson(json: String): ArrayList<Track> {
        return Gson().fromJson(json, object : TypeToken<ArrayList<Track>>() {}.type)
    }*/

    private fun updateHistory(tracks: List<Track>) {
        hideKeyboardAndClearFocus()
        historyTrackList.tracks = tracks.toMutableList() as ArrayList
        historyTrackList.notifyDataSetChanged()
    }

    private fun hideKeyboardAndClearFocus() {
        editText?.let {
            val inputMethodManager = context.getSystemService(InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }
    }

}
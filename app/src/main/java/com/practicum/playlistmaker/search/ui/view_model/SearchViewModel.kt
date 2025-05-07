package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val tracksInteractor = Creator.provideTracksInteractor(getApplication())
    private val tracks = mutableListOf<Track>()

    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null
    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    private val stateLaveData = MutableLiveData<TracksState>()
    fun observerState(): LiveData<TracksState> = stateLaveData

    init {
        renderState(TracksState.History(tracksInteractor.getTracks()))
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(this[APPLICATION_KEY] as App)
            }
        }
    }

    //  Метод для поиска треков
    private fun searchRequest(text: String) {

        tracksInteractor.searchTracks(
            text = text,
            consumer = object : TracksInteractor.TracksConsumer{
                override fun consume(foundTracks: Resource<List<Track>>) {

                    when (foundTracks) {
                        is Resource.Success -> {

                            if (foundTracks.data.isNotEmpty() && text.isNotEmpty()) {
                                tracks.clear()
                                tracks.addAll(foundTracks.data)
                                renderState(TracksState.Content(tracks))
                            } else if (text.isNotEmpty()) {
                                renderState(TracksState.Empty)
                            } else {
                                renderState(TracksState.History(tracksInteractor.getTracks()))
                            }
                        }
                        else -> {
                            if (text.isNotEmpty()) {
                                renderState(TracksState.Error)
                            } else {
                                renderState(TracksState.History(tracksInteractor.getTracks()))
                            }
                        }
                    }
                }

            }
        )
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) return

        lastSearchText = changedText
        if (lastSearchText.isNullOrEmpty()) {
            handler.removeCallbacks(searchRunnable)
            handler.post(searchRunnable)
        } else {
            renderState(TracksState.Loading)
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, 2000)
        }
    }

    fun updateList() {
        if (lastSearchText.isNullOrEmpty()) {
            handler.removeCallbacks(searchRunnable)
            handler.post(searchRunnable)
        } else {
            renderState(TracksState.Loading)
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, 2000)
        }
    }

    fun saveTrack(track: Track) {
        tracksInteractor.saveTrack(track)
    }

    private fun renderState(state: TracksState) {
        stateLaveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

}
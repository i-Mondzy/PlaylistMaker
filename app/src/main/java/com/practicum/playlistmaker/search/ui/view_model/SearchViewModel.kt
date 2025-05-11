package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val tracks = mutableListOf<Track>()

    private val handler = Handler(Looper.getMainLooper())

    private var lastSearchText: String? = null
    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observerState(): LiveData<TracksState> = stateLiveData

    init {
        renderState(TracksState.History(tracksInteractor.getTracks()))
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel((this[APPLICATION_KEY] as App).creator.provideTracksInteractor())
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
        executeSearch()
    }

    fun updateSearch() {
        executeSearch()
    }

    fun updateHistory() {
        handler.postDelayed(
            { renderState(TracksState.History(tracksInteractor.getTracks())) },
            1000
        )
    }

    fun clearHistory() {
        tracksInteractor.clearTracks()
        renderState(TracksState.History(tracksInteractor.getTracks()))
    }

    fun saveTrack(track: Track) {
        tracksInteractor.saveTrack(track)
    }

    private fun executeSearch() {
        handler.removeCallbacks(searchRunnable)
        if (lastSearchText.isNullOrEmpty()) {
            handler.post(searchRunnable)
        } else {
            renderState(TracksState.Loading)
            handler.postDelayed(searchRunnable, 2000)
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

}
package com.practicum.playlistmaker.search.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.ui.state.PlayerState
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.also

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val tracks = mutableListOf<Track>()

    private var lastSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(
        2000,
        viewModelScope,
        true
    ) { search ->
        searchRequest(search)
    }

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observerState(): LiveData<TracksState> = mediatorLiveData

    private val mediatorLiveData = MediatorLiveData<TracksState>().also { liveData ->
        liveData.addSource(stateLiveData) { state ->
            liveData.value = when (state) {
                TracksState.Loading -> state
                TracksState.Empty -> state
                TracksState.Error -> state
                is TracksState.Content -> state
                is TracksState.History -> {
                    Log.d("TrackState", "History:${state.tracks.map { listOf(it.trackName, it.isFavorite) }}")
                    state
                }
            }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            renderState(TracksState.History(tracksInteractor.getTracks()))
            Log.d("init", "init")
        }
    }

    //  Метод для поиска треков
    private fun searchRequest(text: String) {

        viewModelScope.launch {
            tracksInteractor
                .searchTracks(text)
                .collect {
                        resource -> processResult(text, resource)
                }
        }
    }

    private fun processResult(text: String, foundTracks: Resource<List<Track>>) {
        when (foundTracks) {
            is Resource.Success -> {

                if (foundTracks.data.isNotEmpty() && text.isNotEmpty()) {
                    tracks.clear()
                    tracks.addAll(foundTracks.data)
                    renderState(TracksState.Content(tracks))
                } else if (text.isNotEmpty()) {
                    renderState(TracksState.Empty)
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        renderState(TracksState.History(tracksInteractor.getTracks()))
                        Log.d("success", "success")
                    }
                }
            }
            else -> {
                if (text.isNotEmpty()) {
                    renderState(TracksState.Error)
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        renderState(TracksState.History(tracksInteractor.getTracks()))
                        Log.d("error", "error")
                    }
                }
            }
        }
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
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            renderState(TracksState.History(tracksInteractor.getTracks()))
            Log.d("update", "update")
        }
    }

    fun clearHistory() {
        tracksInteractor.clearTracks()
        viewModelScope.launch(Dispatchers.IO) {
            renderState(TracksState.History(tracksInteractor.getTracks()))
        }
    }

    fun saveTrack(track: Track) {
        tracksInteractor.saveTrack(track)
    }

    private fun executeSearch() {
        if (lastSearchText.isNullOrEmpty()) {
            searchRequest(lastSearchText ?: "")
        } else {
            renderState(TracksState.Loading)
            trackSearchDebounce(lastSearchText ?: "")
        }
    }

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

}

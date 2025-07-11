package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    fun observerState(): LiveData<TracksState> = stateLiveData

    init {
        renderState(TracksState.History(tracksInteractor.getTracks()))
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

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) return

        lastSearchText = changedText
        executeSearch()
    }

    fun updateSearch() {
        executeSearch()
    }

    fun updateHistory() {
        viewModelScope.launch {
            delay(1000)
            renderState(TracksState.History(tracksInteractor.getTracks()))
        }
    }

    fun clearHistory() {
        tracksInteractor.clearTracks()
        renderState(TracksState.History(tracksInteractor.getTracks()))
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
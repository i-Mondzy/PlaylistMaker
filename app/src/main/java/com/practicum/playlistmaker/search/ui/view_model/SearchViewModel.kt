package com.practicum.playlistmaker.search.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val tracks = mutableListOf<Track>()

    private var lastSearchText: String? = null

    private val trackSearchDebounce = debounce<String>(
        2000,
        viewModelScope,
        true
    ) { search ->
        if (lastSearchText.isNullOrEmpty()){
            return@debounce
        }
        searchRequest(search)
    }

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observerState(): LiveData<TracksState> = stateLiveData

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()
    fun onTextChanged(newText: String) {
        _text.value = newText
    }

    init {
        Log.d("init", "renderState")

        viewModelScope.launch(Dispatchers.IO) {
            combine(
                tracksInteractor.getTracks(),
                text
            ) { tracks, searchText ->
                tracks to searchText
            }
                .filter { (_, searchText) -> searchText.isEmpty() }
                .collect { (tracks, _) -> renderState(TracksState.History(tracks)) }
        }
    }

    /*init {
        Log.d("init", "renderState")
        viewModelScope.launch(Dispatchers.IO) {
            tracksInteractor
                .getTracks()
                .collect {
                    if (lastSearchText.isNullOrEmpty()) {
                        renderState(TracksState.History(it))
                    }
                }
        }
    }*/

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
                        renderState(TracksState.History(tracksInteractor.getTracks().first()))
                    }
                }
            }
            else -> {
                if (text.isNotEmpty()) {
                    renderState(TracksState.Error)
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        renderState(TracksState.History(tracksInteractor.getTracks().first()))
                    }
                }
            }
        }

        /*when (foundTracks) {
            is Resource.Success -> {

                if (foundTracks.data.isNotEmpty() && text.isNotEmpty()) {
                    tracks.clear()
                    tracks.addAll(foundTracks.data)
                    renderState(TracksState.Content(tracks))
                } else if (text.isNotEmpty()) {
                    renderState(TracksState.Empty)
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        tracksInteractor
                            .getTracks()
                            .collect {
                                renderState(TracksState.History(it))
                            }
                    }
                }
            }
            else -> {
                if (text.isNotEmpty()) {
                    renderState(TracksState.Error)
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        tracksInteractor
                            .getTracks()
                            .collect {
                                renderState(TracksState.History(it))
                            }
                    }
                }
            }
        }*/

        /*when (foundTracks) {
            is Resource.Success -> {
                if (foundTracks.data.isEmpty()) {
                    renderState(TracksState.Empty)
                } else {
                    tracks.clear()
                    tracks.addAll(foundTracks.data)
                    renderState(TracksState.Content(tracks))
                }
            }
            is Resource.Error -> {
                renderState(TracksState.Error)
            }
        }*/
    }

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) return

        lastSearchText = changedText
        executeSearch()
    }

    fun updateSearch() {
        executeSearch()
    }

    /*    fun updateHistory() {
            viewModelScope.launch(Dispatchers.IO) {
                delay(1000)
                renderState(TracksState.History(tracksInteractor.getTracks().first()))
            }
        }

        fun refreshHistory() {
            viewModelScope.launch(Dispatchers.IO) {
                renderState(TracksState.History(tracksInteractor.getTracks().first()))
            }
        }*/

    fun clearHistory() {
        tracksInteractor.clearTracks()
        viewModelScope.launch(Dispatchers.IO) {
            tracksInteractor
                .getTracks()
                .collect {
                    renderState(TracksState.History(it))
                }
        }
    }

    suspend fun saveTrack(track: Track) {
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

package com.practicum.playlistmaker.media.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.db.domain.FavoriteInteractor
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteViewModule(private val favoriteInteractor: FavoriteInteractor) : ViewModel() {

    private val tracks = mutableListOf<Track>()

    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observerState(): LiveData<FavoriteState> = stateLiveData

    init {
        showFavorite()
    }

    fun refreshFavorites() {
        tracks.clear()
        showFavorite()
    }

    private fun showFavorite() {
        viewModelScope.launch {
            favoriteInteractor
                .getTracks()
                .collect {
                    it -> tracks.addAll(it)
                }

            if (tracks.isEmpty()) {
                renderState(FavoriteState.Empty)
            } else {
                renderState(FavoriteState.Content(tracks.map { track ->
                    track.copy(isFavorite = true)
                }))
            }
        }
    }

    private fun renderState(state: FavoriteState) {
        stateLiveData.postValue(state)
    }

}

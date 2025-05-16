package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.model.Track
import java.util.concurrent.Executor

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val executor: Executor
) : TracksInteractor {

    override fun searchTracks(text: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute{

            consumer.consume(repository.searchTracks(text))

        }
    }

    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getTracks(): List<Track> {
        return repository.getTracks()
    }

    override fun clearTracks() {
        repository.clearTracks()
    }

}
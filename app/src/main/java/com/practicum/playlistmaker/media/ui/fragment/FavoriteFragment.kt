package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteViewModule
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : BindingFragment<FragmentFavoriteBinding>() {

    private val viewModel by viewModel<FavoriteViewModule>()

    private var trackIndex: Int? = null
    private var init = false

    private val favoriteTracks = TrackAdapter{ track, position ->
        trackIndex = position
        openPlayer(track)
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(R.id.action_mediaFragment_to_playerFragment, PlayerFragment.createArgs(track))
    }

    private fun render(state: FavoriteState) {
        when(state) {
            is FavoriteState.Content -> {
                init = true
                showFavoriteTracks(state.tracks)
            }
            FavoriteState.Empty -> {
                init = true
                showMessageEmpty()
            }
        }
    }

    private fun showMessageEmpty() {
        binding.placeholderEmpty.isVisible = true
        binding.favoriteTracks.isVisible = false
    }

    private fun showFavoriteTracks(tracks: List<Track>) {
        favoriteTracks.tracks.clear()
        favoriteTracks.tracks.addAll(tracks)
        favoriteTracks.notifyDataSetChanged()
        binding.favoriteTracks.scrollToPosition(0)

        binding.placeholderEmpty.isVisible = false
        binding.favoriteTracks.isVisible = true
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeholderEmpty.isVisible = false
        binding.favoriteTracks.isVisible = false

        binding.favoriteTracks.adapter = favoriteTracks
        binding.favoriteTracks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        viewModel.observerState().observe(viewLifecycleOwner) {
            render(it)
        }

        /*val upd = findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Pair<Long, Boolean>>("update")
        Log.d("FAVupd", "$upd")
        upd
            ?.observe(viewLifecycleOwner) { (trackId, isFavorite) ->
                Log.d("FAVupdated", "${favoriteTracks.tracks.indexOfFirst { it.trackId == trackId }}")
                trackIndex?.let { index ->
                    if (binding.favoriteTracks.isVisible) {
                        favoriteTracks.tracks[favoriteTracks.tracks.indexOfFirst { it.trackId == trackId }] = favoriteTracks.tracks[favoriteTracks.tracks.indexOfFirst { it.trackId == trackId }].copy(isFavorite = isFavorite)
                        Log.d("FAVfav", "${favoriteTracks.tracks[favoriteTracks.tracks.indexOfFirst { it.trackId == trackId }].isFavorite}")
                    }
                    if (!favoriteTracks.tracks[favoriteTracks.tracks.indexOfFirst { it.trackId == trackId }].isFavorite) {
                        favoriteTracks.tracks.removeAt(favoriteTracks.tracks.indexOfFirst { it.trackId == trackId })
                        viewModel.updateFavorite(favoriteTracks.tracks)
                    }
                }
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Pair<Long, Boolean>>("update")
            }*/

    }

    override fun onResume() {
        super.onResume()
        if (init) {
            viewModel.refreshFavorites()
        }
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }

}

package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.media.ui.compose_ui.MediaScreen
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.media.ui.view_model.FavoriteViewModule
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.activity.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private val viewModel by viewModel<FavoriteViewModule>()
    private lateinit var binding: FragmentFavoriteBinding

    private var trackIndex: Int? = null
    private var init = false

    private val favoriteTracks = TrackAdapter { track, position ->
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

    /*override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavoriteBinding {
        return FragmentFavoriteBinding.inflate(inflater, container, false)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen()
            }
        }
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

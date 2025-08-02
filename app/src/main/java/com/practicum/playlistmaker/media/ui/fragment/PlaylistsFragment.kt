package com.practicum.playlistmaker.media.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.ui.view_model.PlaylistsViewModule
import com.practicum.playlistmaker.utils.BindingFragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.media.ui.state.FavoriteState
import com.practicum.playlistmaker.media.ui.state.PlaylistsState
import com.practicum.playlistmaker.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BindingFragment<FragmentPlaylistsBinding>() {

    private val viewModel by viewModel<PlaylistsViewModule>()

    private val playlists = PlaylistsAdapter()

    private fun render(state: PlaylistsState) {
        when(state) {
            is PlaylistsState.Content -> {
                showPlaylists(state.playlists)
            }
            PlaylistsState.Empty -> {
                showMessageEmpty()
            }
        }
    }

    private fun showMessageEmpty() {
        binding.placeholderEmpty.isVisible = true
        binding.playlists.isVisible = false
    }

    private fun showPlaylists(playlist: List<Playlist>) {
        playlists.playlists.clear()
        playlists.playlists.addAll(playlist)
        playlists.notifyDataSetChanged()
        binding.playlists.scrollToPosition(0)

        binding.placeholderEmpty.isVisible = false
        binding.playlists.isVisible = true
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlists.adapter = playlists
        binding.playlists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlists.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view) // Позиция элемента в списке
                if (position == RecyclerView.NO_POSITION) return

                val column = position % 2 // Определяем столбец

//                val halfSpacing = binding.playlists.adapter.dpToPx(16f, view.context) / 2

                // Убираем отступы у краёв, но оставляем между элементами
                outRect.left = if (column == 0) 0 else 8
                outRect.right = if (column == 2 - 1) 0 else 8
            }
        })

        binding.newPlaylistBtn.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylist)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

}

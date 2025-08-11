package com.practicum.playlistmaker.create_playlist.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.create_playlist.ui.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi
import com.practicum.playlistmaker.playlist.ui.state.PlaylistState
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : CreatePlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    private fun setUi(playlistUi: PlaylistUi?) {
        binding.newPlaylist.text = "Редактировать плейлист"

        if (playlistUi?.imgPath?.isNotEmpty() == true) {
                binding.imgPlaylist.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                binding.imgPlaylist.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

            Glide.with(this)
                .load(playlistUi.imgPath)
                .centerCrop()
                .into(binding.imgPlaylist)
        }

        binding.namePlaylist.setText(playlistUi?.namePlaylist)
        binding.descriptionPlaylist.setText(playlistUi?.description)
        binding.createPlaylist.text = "Сохранить"
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> setUi(state.playlistUi)
            PlaylistState.Empty -> ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("onViewCreated", "onViewCreated")

        requireArguments().getParcelable<PlaylistUi>(ARGS_PLAYLIST)?.let {
            Log.d("argument", "${it.playlistId}")
            viewModel.setUi(it)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    companion object {
        private const val ARGS_PLAYLIST = "Playlist"

        fun createArgs(playlistUi: PlaylistUi): Bundle = bundleOf(ARGS_PLAYLIST to playlistUi)
    }

}

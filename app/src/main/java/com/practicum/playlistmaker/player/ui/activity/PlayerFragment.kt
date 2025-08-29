package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.create_playlist.domain.model.Playlist
import com.practicum.playlistmaker.create_playlist.ui.fragment.CreatePlaylistFragment
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import com.practicum.playlistmaker.player.ui.state.PlayerStateBottomSheet
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var trackUi: TrackUi
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private var clickPlay = false

    private val playlistsAdapter = PlayerPlaylistsAdapter{ position ->
        requireArguments().getParcelable<Track>(ARGS_TRACK)?.let { track ->
            saveToPlaylist(track, position)
        }
    }

    private fun setUi(trackUi: TrackUi?) {
        if (trackUi == null) return

        this@PlayerFragment.trackUi = trackUi

        Glide.with(this)
            .load(trackUi.artworkUrl100)
            .placeholder(R.drawable.plug_artwork_high)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, requireContext())))
            .into(binding.artwork)

        binding.trackName.text = trackUi.trackName
        binding.artistName.text = trackUi.artistName
        binding.currentTrackTime.text = trackUi.currentTime
        binding.trackTimeValue.text = trackUi.trackTimeMillis
        binding.collectionNameValue.text = trackUi.collectionName
        binding.releaseDateValue.text = trackUi.releaseDate
        binding.primaryGenreNameValue.text = trackUi.primaryGenreName
        binding.countryValue.text = trackUi.country

        binding.playButton.setCurrentButton(false)

        when (trackUi.isFavorite) {
            true -> binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_true)
            false -> binding.addToFavorite.setImageResource(R.drawable.ic_add_to_favorite_false)
        }

        when (binding.collectionNameValue.text) {
            "" -> binding.collectionGroup.isVisible = false
            else -> binding.collectionGroup.isVisible = true
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()

        return px
    }

    private fun showPlay(state: PlayerState) {
        binding.playButton.setCurrentButton(true)
        binding.currentTrackTime.text = (state as? PlayerState.Play)?.time
    }

    private fun showPause(state: PlayerState) {
        binding.playButton.setCurrentButton(false)
        binding.currentTrackTime.text = (state as? PlayerState.Pause)?.time
    }

    private fun showStop(state: PlayerState) {
        binding.playButton.setCurrentButton(false)
        binding.currentTrackTime.text = (state as? PlayerState.Stop)?.time
    }

    private fun showBS(playlist: List<Playlist>) {
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlist)

        playlistsAdapter.notifyDataSetChanged()
        binding.playlistsBottomSheet.scrollToPosition(0)
    }

    private fun saveToPlaylist(track: Track, position: Int) {
        if (!playlistsAdapter.playlists[position].trackList.contains(track.trackId)) {
            viewModel.onPlaylistClicked(track, position)
            Toast
                .makeText(
                    requireContext(),
                    "Добавлено в плейлист ${playlistsAdapter.playlists[position].namePlaylist}",
                    Toast.LENGTH_SHORT
                )
                .show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            Toast
                .makeText(
                    requireContext(),
                    "Трек уже добавлен в плейлист ${playlistsAdapter.playlists[position].namePlaylist}",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> setUi(state.track)
            is PlayerState.Play -> showPlay(state)
            is PlayerState.Pause -> showPause(state)
            is PlayerState.Stop -> showStop(state)
        }
    }

    private fun renderBS(state: PlayerStateBottomSheet) {
        Log.d("renderBS", "renderBS")
        when (state) {
            is PlayerStateBottomSheet.Content -> {
                showBS(state.playlists)
            }
        }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsBottomSheet.adapter = playlistsAdapter
        binding.playlistsBottomSheet.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        requireArguments().getParcelable<Track>(ARGS_TRACK)?.let { viewModel.setTrack(it) }
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.getStateLiveDataBS().observe(viewLifecycleOwner) { state ->
            renderBS(state)
        }

//      Уменьшить обложку если маленький экран
        binding.currentTrackTime.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.currentTrackTime.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val trackTimeLocation = IntArray(2)
                binding.currentTrackTime.getLocationOnScreen(trackTimeLocation)
                val trackTimeY = trackTimeLocation[1]
                val screenHeight = resources.displayMetrics.heightPixels

                if (trackTimeY + binding.currentTrackTime.height > screenHeight) {
                    binding.artwork.layoutParams.height = max(150, binding.artwork.height - 50)
                    binding.artwork.requestLayout()
                }
            }
        })

        binding.playButton.setOnClickListener{
            clickPlay = true
            viewModel.playbackControl()
        }

        binding.addToFavorite.setOnClickListener{
            requireArguments().getParcelable<Track>(ARGS_TRACK)?.let { viewModel.onFavoriteClicked(it.copy(isFavorite = trackUi.isFavorite)) }
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.addToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.createPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playerFragment_to_createPlaylist,
                CreatePlaylistFragment.createArgs(null)
            )
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

    }

    override fun onPause() {
        super.onPause()
        if (clickPlay) {
            viewModel.pause()
        }
    }

    companion object {
        private const val ARGS_TRACK = "Track"

        fun createArgs(trackId: Track): Bundle = bundleOf(ARGS_TRACK to trackId)
    }

}

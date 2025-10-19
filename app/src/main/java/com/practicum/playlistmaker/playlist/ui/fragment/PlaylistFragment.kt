package com.practicum.playlistmaker.playlist.ui.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.create_playlist.ui.fragment.CreatePlaylistFragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.playlist.ui.model.PlaylistUi
import com.practicum.playlistmaker.playlist.ui.state.PlaylistState
import com.practicum.playlistmaker.playlist.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var playlistUi: PlaylistUi
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var otherBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var init = false

    private val tracksAdapter = TrackAdapter(object : TrackAdapter.TrackClickListener {
        override fun onTrackClickListener(track: Track, position: Int) {
            openPlayer(track)
        }

        override fun onTrackLongClickListener(track: Track, position: Int) {
            confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.playlistDeleteTrack)
                .setNegativeButton(R.string.playlistNo) { dialog, which -> }
                .setPositiveButton(R.string.playlistYes) { dialog, which ->
                    viewModel.deleteTrack(track)
                }
            confirmDialog.show()
        }
    })

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    fun setUi(playlistUi: PlaylistUi?) {
        if (playlistUi == null) return

        this.playlistUi = playlistUi

        Glide.with(this)
            .load(playlistUi.imgPath)
            .placeholder(R.drawable.plug_artwork_high)
            .centerCrop()
            .into(binding.imgPlaylist)

        binding.namePlaylist.text = playlistUi.namePlaylist
        binding.descriptionPlaylist.text = playlistUi.description
        binding.minutes.text = wordMinutes(playlistUi.tracksTime.toInt())
        binding.countTracks.text = wordTracks(playlistUi.tracksCount.toInt())
        setTracks(playlistUi.trackList)

        if (playlistUi.trackList.isEmpty()) {
            Toast.makeText(requireContext(), "В этом плейлисте нет треков", Toast.LENGTH_SHORT).show()
        }

        Glide.with(this)
            .load(playlistUi.imgPath)
            .placeholder(R.drawable.plug_artwork_low)
            .centerCrop()
            .into(binding.imgPlaylistBottomSheet)

        binding.namePlaylistBottomSheet.text = playlistUi.namePlaylist
        binding.countTracksBottomSheet.text = wordTracks(playlistUi.tracksCount.toInt())
    }

    private fun wordTracks(count: Int): String {
        val lastTwoDigits = count % 100
        val lastDigit = count % 10

        val word = when {
            lastTwoDigits in 11..14 -> "треков"
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            else -> "треков"
        }

        return "$count $word"
    }

    private fun wordMinutes(minute: Int): String {
        val lastTwoDigits = minute % 100
        val lastDigit = minute % 10

        val word = when {
            lastTwoDigits in 11..14 -> "минут"
            lastDigit == 1 -> "минута"
            lastDigit in 2..4 -> "минуты"
            else -> "минут"
        }

        return "$minute $word"
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> {
                init = true
                setUi(state.playlistUi)
            }
            PlaylistState.Empty -> ""
        }
    }

    private fun setTracks(tracks: List<Track>) {
        tracksAdapter.tracks.apply {
            clear()
            addAll(tracks)
        }
        tracksAdapter.notifyDataSetChanged()
        binding.tracksRV.scrollToPosition(0)
    }

    private fun dynamicRenderHeightBottomSheet() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)

        val locationTracks = IntArray(2)
        val locationOther = IntArray(2)
        binding.share.getLocationOnScreen(locationTracks)
        binding.namePlaylist.getLocationOnScreen(locationOther)
        val bottomShareBtn = locationTracks[1] + binding.share.height
        val bottomNamePlaylist = locationOther[1] + binding.namePlaylist.height

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        tracksBottomSheetBehavior.peekHeight =
            screenHeight - bottomShareBtn - dpToPx(24f, requireContext())
        otherBottomSheetBehavior.peekHeight = screenHeight - bottomNamePlaylist
        tracksBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()

        return px
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         if (!init) {
             requireArguments().getLong(ARGS_PLAYLIST).let { viewModel.setPlaylist(it) }
         }

        binding.share.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.share.viewTreeObserver.removeOnGlobalLayoutListener(this)
                dynamicRenderHeightBottomSheet()
            }
        })

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        otherBottomSheetBehavior = BottomSheetBehavior.from(binding.otherBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        otherBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
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
        binding.overlay.setOnTouchListener { _, _ -> true }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tracksRV.adapter = tracksAdapter
        binding.tracksRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.other.setOnClickListener {
            otherBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.share.setOnClickListener {
            if (playlistUi.trackList.isNotEmpty()) {
                viewModel.share()
            } else {
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.shareBottomSheet.setOnClickListener {
            if (playlistUi.trackList.isNotEmpty()) {
                otherBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                viewModel.share()
            } else {
                otherBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.editPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_createPlaylist,
                CreatePlaylistFragment.createArgs(playlistUi)
            )
        }

        binding.deletePlaylist.setOnClickListener {
            otherBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            confirmDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.playlistDeletePlaylist, playlistUi.namePlaylist))
                .setNegativeButton(R.string.playlistNo) { dialog, which -> }
                .setPositiveButton(R.string.playlistYes) { dialog, which ->
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
            confirmDialog.show()
        }

    }

    override fun onResume() {
        super.onResume()

        if (init) {
            viewModel.updatePlaylist()
        }
    }

    companion object {
        private const val ARGS_PLAYLIST = "Playlist"

        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS_PLAYLIST to playlistId)
    }

}

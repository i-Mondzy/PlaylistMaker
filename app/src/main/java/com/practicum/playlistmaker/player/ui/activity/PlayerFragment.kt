package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.max

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var trackUi: TrackUi

    private fun setUi(trackUi: TrackUi?) {
        if (trackUi == null) return

        findNavController().previousBackStackEntry
            ?.savedStateHandle
            ?.set("update", trackUi.trackId to trackUi.isFavorite)

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
        binding.playButton.setImageDrawable(getDrawable(requireContext(), R.drawable.ic_pause))
        binding.currentTrackTime.text = (state as? PlayerState.Play)?.time
    }

    private fun showPause(state: PlayerState) {
        binding.playButton.setImageDrawable(getDrawable(requireContext(), R.drawable.ic_play))
        binding.currentTrackTime.text = (state as? PlayerState.Pause)?.time
    }

    private fun showStop(state: PlayerState) {
        binding.playButton.setImageDrawable(getDrawable(requireContext(), R.drawable.ic_play))
        binding.currentTrackTime.text = (state as? PlayerState.Stop)?.time
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> setUi(state.track)
            is PlayerState.Play -> showPlay(state)
            is PlayerState.Pause -> showPause(state)
            is PlayerState.Stop -> showStop(state)
        }
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().getParcelable<Track>(ARGS_TRACK)?.let { viewModel.setTrack(it) }
        viewModel.getStateLiveData().observe(viewLifecycleOwner) { state ->
            render(state)
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
            viewModel.playbackControl()
        }

        binding.addToFavorite.setOnClickListener{
            requireArguments().getParcelable<Track>(ARGS_TRACK)?.let { viewModel.onFavoriteClicked(it.copy(isFavorite = trackUi.isFavorite)) }
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    companion object {
        private const val ARGS_TRACK = "Track"

        fun createArgs(trackId: Track): Bundle = bundleOf(ARGS_TRACK to trackId)
    }

}

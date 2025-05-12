package com.practicum.playlistmaker.player.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.state.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import kotlin.math.max

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by lazy {
        ViewModelProvider(this, PlayerViewModel.getViewModelFactory())[PlayerViewModel::class.java]
    }

    private fun setUi(track: TrackUi?) {

        if (track != null) {
            Glide.with(this)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.plug_artwork_high)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f, this)))
                .into(binding.artwork)

            binding.trackName.text = track.trackName
            binding.artistName.text = track.artistName
            binding.currentTrackTime.text = track.currentTime
            binding.trackTimeValue.text = track.trackTimeMillis
            binding.collectionNameValue.text = track.collectionName
            binding.releaseDateValue.text = track.releaseDate
            binding.primaryGenreNameValue.text = track.primaryGenreName
            binding.countryValue.text = track.country

            when (binding.collectionNameValue.text) {
                "" -> binding.collectionGroup.isVisible = false
                else -> binding.collectionGroup.isVisible = true
            }
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
        binding.playButton.setImageDrawable(getDrawable(R.drawable.ic_pause))
        binding.currentTrackTime.text = (state as? PlayerState.Play)?.time
    }

    private fun showPause() {
        binding.playButton.setImageDrawable(getDrawable(R.drawable.ic_play))
    }

    private fun showStop(state: PlayerState) {
        binding.playButton.setImageDrawable(getDrawable(R.drawable.ic_play))
        binding.currentTrackTime.text = (state as? PlayerState.Stop)?.time
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> setUi(state.track)
            is PlayerState.Play -> showPlay(state)
            PlayerState.Pause -> showPause()
            is PlayerState.Stop -> showStop(state)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        intent.getParcelableExtra<Track>("TRACK")?.let { viewModel.setTrack(it) }
        viewModel.getStateLiveData().observe(this) { state ->
            render(state)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

}















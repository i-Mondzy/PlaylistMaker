package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import kotlin.math.max

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 3
        private const val STATE_PAUSED = 4

        private const val DEF_TIME = 30L
    }

    private lateinit var artwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playBtn: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private var seconds = 30
    private var pauseSeconds = 30
    private var startTime = 0L
    private var pauseTime = 0L
    private var remainingMillis = 1000L

    private val playRunnable = object : Runnable {
        override fun run() {
            startTime = System.currentTimeMillis()

            seconds -= 1

            currentTrackTime.text = String.format("%02d:%02d", seconds / 60, seconds)
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish()
        }

        artwork = findViewById(R.id.artwork)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        playBtn = findViewById(R.id.playButton)
        currentTrackTime = findViewById(R.id.currentTrackTime)
        currentTrackTime = findViewById(R.id.currentTrackTime)
        trackTime = findViewById(R.id.trackTimeValue)
        collectionName = findViewById(R.id.collectionNameValue)
        releaseDate = findViewById(R.id.releaseDateValue)
        primaryGenreName = findViewById(R.id.primaryGenreNameValue)
        country = findViewById(R.id.countryValue)

        intent?.let {
            val artworkUrl = it.getStringExtra("TRACK_ARTWORK")
            Glide.with(this)
                .load(artworkUrl)
                .placeholder(R.drawable.plug_artwork_high)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f, this)))
                .into(artwork)

            preparePlayer(it.getStringExtra("PREVIEW") ?: "")

            trackName.text = it.getStringExtra("TRACK_NAME") ?: "Unknown"
            artistName.text = it.getStringExtra("ARTIST_NAME") ?: "Unknown"
            trackTime.text = it.getStringExtra("TRACK_TIME") ?: "0"
            collectionName.text = it.getStringExtra("COLLECTION_NAME") ?: "Unknown"
            releaseDate.text = it.getStringExtra("RELEASE_DATE")?.take(4) ?: "Unknown"
            primaryGenreName.text = it.getStringExtra("PRIMARY_GENRE_NAME") ?: "Unknown"
            country.text = it.getStringExtra("COUNTRY") ?: "Unknown"
        }

        when (collectionName.text) {
            "Unknown" -> findViewById<Group>(R.id.collectionGroup).visibility = View.GONE
            else -> findViewById<Group>(R.id.collectionGroup).visibility = View.VISIBLE
        }

//      Уменьшить обложку если маленький экран
        currentTrackTime.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                currentTrackTime.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val trackTimeLocation = IntArray(2)
                currentTrackTime.getLocationOnScreen(trackTimeLocation)
                val trackTimeY = trackTimeLocation[1]
                Log.d("list", listToString(trackTimeLocation))
                val screenHeight = resources.displayMetrics.heightPixels

                if (trackTimeY + currentTrackTime.height > screenHeight) {
                    artwork.layoutParams.height = max(150, artwork.height - 50)
                    artwork.requestLayout()
                }
            }
        })

        playBtn.setOnClickListener{
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()

        /*Log.d("dpToPX", "Пиксели равны: $px")*/

        return px
    }

    private fun listToString(list: IntArray): String {
        return list.joinToString(", ")
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        currentTrackTime.text = String.format("%02d:%02d", 0, DEF_TIME)

        mediaPlayer.setOnPreparedListener {
            playBtn.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener{
            currentTrackTime.text = String.format("%02d:%02d", 0, 0)
            playBtn.setImageDrawable(getDrawable(R.drawable.ic_play))
            remainingMillis = 1000L
            seconds = 30
            handler.removeCallbacks(playRunnable)
            playerState = STATE_PREPARED

        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playBtn.setImageDrawable(getDrawable(R.drawable.ic_pause))
        playerState = STATE_PLAYING

        startTime = System.currentTimeMillis()

        if (seconds == 30) currentTrackTime.text = String.format("%02d:%02d", 0, DEF_TIME)

        handler.postDelayed(playRunnable, remainingMillis)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playBtn.setImageDrawable(getDrawable(R.drawable.ic_play))
        playerState = STATE_PAUSED

        pauseTime = System.currentTimeMillis()

        if (pauseSeconds != seconds) {
            remainingMillis = 1000L - (pauseTime - startTime)
        } else {
            remainingMillis -= (pauseTime - startTime)
        }

        pauseSeconds = seconds
        handler.removeCallbacks(playRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED -> {
                startPlayer()
            }

            STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}















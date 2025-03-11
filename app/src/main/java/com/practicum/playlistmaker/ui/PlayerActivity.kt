package com.practicum.playlistmaker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
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
import com.practicum.playlistmaker.data.recycler.Track
import kotlin.math.max

class PlayerActivity : AppCompatActivity() {

    private lateinit var artwork: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var currentTrackTime: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView

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
        currentTrackTime = findViewById(R.id.currentTrackTime)
        trackTime = findViewById(R.id.trackTimeValue)
        collectionName = findViewById(R.id.collectionNameValue)
        releaseDate = findViewById(R.id.releaseDateValue)
        primaryGenreName = findViewById(R.id.primaryGenreNameValue)
        country = findViewById(R.id.countryValue)

        intent?.let {
            val artworkUrl = it.getStringExtra("TRACK_ARTWORK") ?: "" /*"https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/PNG_transparency_demonstration_1.png/800px-PNG_transparency_demonstration_1.png"*/
            Log.d("art", "ссылка = $artworkUrl")
            Glide.with(this)
                .load(artworkUrl)
                .placeholder(R.drawable.plug_artwork_high)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f, this)))
                .into(artwork)

            trackName.text = it.getStringExtra("TRACK_NAME") ?: "Unknown"
            artistName.text = it.getStringExtra("ARTIST_NAME") ?: "Unknown"
            trackTime.text = it.getStringExtra("TRACK_TIME") ?: "0"
            Log.d("TimeGet", "Время: ${trackTime.text}")

            collectionName.text = it.getStringExtra("COLLECTION_NAME") ?: "Unknown"
            releaseDate.text = it.getStringExtra("RELEASE_DATE")?.take(4) ?: "Unknown"
            Log.d("DateGet", "Время: ${releaseDate.text}")

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

                val screenHeight = resources.displayMetrics.heightPixels

                if (trackTimeY + currentTrackTime.height > screenHeight) {
                    artwork.layoutParams.height = max(150, artwork.height - 50)
                    artwork.requestLayout()
                }
            }
        })
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()

        Log.d("dpToPX", "Пиксели равны: $px")

        return px
    }
}
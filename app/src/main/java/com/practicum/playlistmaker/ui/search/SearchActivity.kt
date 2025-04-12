package com.practicum.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.TimingInterceptor
import com.practicum.playlistmaker.domain.model.Track
import com.practicum.playlistmaker.data.network.TrackApi
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.ui.player.PlayerActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    var inputText = STRING_DEF

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val STRING_DEF = ""
    }

    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderMessageNothingFound: LinearLayout
    private lateinit var placeholderMessageNoInternet: LinearLayout
    private lateinit var history: LinearLayout
    private lateinit var searchTracksListRV: RecyclerView
    private lateinit var historyTracksListRV: RecyclerView
    private lateinit var updateBtn: Button
    private lateinit var clearHistoryBtn: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var searchHistory: SearchHistory

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { showTracks() }

    private val searchTrackList = TrackAdapter { track ->
        searchHistory.saveTrack(track)
        startActivity(
            Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK_ARTWORK", track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                putExtra("TRACK_NAME", track.trackName)
                putExtra("ARTIST_NAME", track.artistName)
                putExtra("TRACK_TIME", SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong()))
                putExtra("COLLECTION_NAME", track.collectionName)
                putExtra("RELEASE_DATE", SimpleDateFormat("yyyy", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(track.releaseDate)))
                putExtra("PRIMARY_GENRE_NAME", track.primaryGenreName)
                putExtra("COUNTRY", track.country)
                putExtra("PREVIEW", track.previewUrl)
            }
        )
    }
    private val tracks = ArrayList<Track>()

    private val baseUrl = "https://itunes.apple.com"
    private val client = OkHttpClient.Builder().addInterceptor(TimingInterceptor()).build()
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(client).build()
    private val trackService = retrofit.create(TrackApi::class.java)

    //  Метод для отображения треков
    private fun showTracks() {
        val query = queryInput.text.toString()

        trackService.search(query)
            .enqueue(object : Callback<TracksSearchResponse> {
                override fun onResponse(
                    call: Call<TracksSearchResponse>,
                    response: Response<TracksSearchResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true && query.isNotEmpty()) {
                                tracks.clear()
                                tracks.addAll(response.body()!!.results)
                                searchTrackList.notifyDataSetChanged()
                                searchTracksListRV.visibility = View.VISIBLE
                                placeholderMessageNoInternet.visibility = View.GONE
                                placeholderMessageNothingFound.visibility = View.GONE
                                history.visibility = View.GONE
                                progressBar.visibility = View.GONE
                            } else if (query.isNotEmpty()) {
                                tracks.clear()
                                searchTrackList.notifyDataSetChanged()
                                placeholderMessageNothingFound.visibility = View.VISIBLE
                                placeholderMessageNoInternet.visibility = View.GONE
                                history.visibility = View.GONE
                                progressBar.visibility = View.GONE
                            } else {
                                tracks.clear()
                                searchTrackList.notifyDataSetChanged()
                                placeholderMessageNothingFound.visibility = View.GONE
                                placeholderMessageNoInternet.visibility = View.GONE
                                progressBar.visibility = View.GONE
                                if (searchHistory.getHistory().isEmpty()) {
                                    history.visibility = View.GONE
                                } else {
                                    history.visibility = View.VISIBLE
                                }
                            }
                        }

                        else -> {
                            if (query.isNotEmpty()) {
                                tracks.clear()
                                searchTrackList.notifyDataSetChanged()
                                placeholderMessageNothingFound.visibility = View.GONE
                                placeholderMessageNoInternet.visibility = View.VISIBLE
                                history.visibility = View.GONE
                                progressBar.visibility = View.GONE
                            } else {
                                tracks.clear()
                                searchTrackList.notifyDataSetChanged()
                                placeholderMessageNothingFound.visibility = View.GONE
                                placeholderMessageNoInternet.visibility = View.GONE
                                progressBar.visibility = View.GONE
                                if (searchHistory.getHistory().isEmpty()) {
                                    history.visibility = View.GONE
                                } else {
                                    history.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                    if (query.isNotEmpty()) {
                        tracks.clear()
                        searchTrackList.notifyDataSetChanged()
                        placeholderMessageNothingFound.visibility = View.GONE
                        placeholderMessageNoInternet.visibility = View.VISIBLE
                        history.visibility = View.GONE
                        progressBar.visibility = View.GONE
                    } else {
                        tracks.clear()
                        searchTrackList.notifyDataSetChanged()
                        placeholderMessageNothingFound.visibility = View.GONE
                        placeholderMessageNoInternet.visibility = View.GONE
                        progressBar.visibility = View.GONE
                        if (searchHistory.getHistory().isEmpty()) {
                            history.visibility = View.GONE
                        } else {
                            history.visibility = View.VISIBLE
                        }
                    }
                }

            })
    }

    // Метод для обновления списка "Поиск"
    private fun update() {
        updateBtn.setOnClickListener {
            showTracks()
            hideKeyboardIfNeeded()
        }
    }

    // Метод для видимости "Прогресс бара"
    private fun progressBarVisibility(s: CharSequence?): Int {
        history.visibility = View.GONE
        searchTracksListRV.visibility = View.GONE
        placeholderMessageNothingFound.visibility = View.GONE
        placeholderMessageNoInternet.visibility = View.GONE

        if (s.isNullOrEmpty()) {
            handler.removeCallbacks(searchRunnable)
            handler.post { showTracks() }
        } else {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, 2000L)
        }

        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    //  Метод для видимости кнопки "Очистить текст"
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    //  Метод для "Скрытия клавиатуры"
    private fun hideKeyboardIfNeeded() {
        val currentFocusView = currentFocus
        if (currentFocusView != null && currentFocusView is EditText) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
            currentFocusView.clearFocus()
        }
    }

    //  Метод для "Сохранения"
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохранить текст
        outState.putString(SEARCH_STRING, inputText)

        // Сохранить список
        outState.putParcelableArrayList("adapter_data", ArrayList(tracks))

        val layoutManager = searchTracksListRV.layoutManager as LinearLayoutManager
        outState.putInt("scroll_position", layoutManager.findFirstVisibleItemPosition())
        layoutManager.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
            ?.let { view ->
                outState.putInt("scroll_offset", view.top - layoutManager.paddingTop)
            }

        outState.putInt("placeholder_nothing_found_visibility", placeholderMessageNothingFound.visibility)
        outState.putInt("placeholder_no_internet_visibility", placeholderMessageNoInternet.visibility)
    }

    //  Метод для "Воостановления"
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Восстановить текст
        inputText = savedInstanceState.getString(SEARCH_STRING, STRING_DEF)

        val editText = findViewById<EditText>(R.id.input_text)
        editText.setText(inputText)
        editText.setSelection(inputText.length)

        // Восстановить список
        val restoredTracks = savedInstanceState.getParcelableArrayList<Track>("adapter_data")
        if (restoredTracks != null) {
            tracks.clear()
            tracks.addAll(restoredTracks)
            searchTrackList.notifyDataSetChanged()
        }

        val scrollPosition = savedInstanceState.getInt("scroll_position", 0)
        val scrollOffset = savedInstanceState.getInt("scroll_offset", 0)
        val layoutManager = searchTracksListRV.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        queryInput = findViewById(R.id.input_text)
        clearButton = findViewById(R.id.clear_text)
        placeholderMessageNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderMessageNoInternet = findViewById(R.id.placeholder_no_internet)
        history = findViewById(R.id.history)
        searchTracksListRV = findViewById(R.id.track_found)
        historyTracksListRV = findViewById(R.id.track_history)
        updateBtn = findViewById(R.id.update_btn)
        clearHistoryBtn = findViewById(R.id.clear_history)
        progressBar = findViewById(R.id.progressBar)

        placeholderMessageNothingFound.visibility = View.GONE
        placeholderMessageNoInternet.visibility = View.GONE
        history.visibility = View.GONE
        progressBar.visibility = View.GONE

        val sharedPrefs = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
        searchHistory = SearchHistory(this, queryInput, sharedPrefs)
        historyTracksListRV.adapter = searchHistory.historyTrackList
        historyTracksListRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

//      Отобразить историю при запуске
        if (searchHistory.getHistory().isNotEmpty()) {
            searchHistory.historyTrackList.tracks = searchHistory.getHistory()
            searchHistory.historyTrackList.notifyItemInserted(0)
            history.visibility = View.VISIBLE
        }

//      Очистить историю
        clearHistoryBtn.setOnClickListener {
            searchHistory.clearHistory()
            searchHistory.historyTrackList.notifyDataSetChanged()
            history.visibility = View.GONE
        }

        // Восстановление видимости
        savedInstanceState?.let {
            placeholderMessageNothingFound.visibility = it.getInt("placeholder_nothing_found_visibility", View.GONE)
            placeholderMessageNoInternet.visibility = it.getInt("placeholder_no_internet_visibility", View.GONE)
        }
        update()

//      Обработчик кнопки "Назад"
        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

//      Обработчик кнопки "Очистить текст" в поиске
        clearButton.setOnClickListener {
            queryInput.setText("")
            queryInput.clearFocus()

            /*tracks.clear()
            searchTrackList.notifyDataSetChanged()
            placeholderMessageNothingFound.visibility = View.GONE
            placeholderMessageNoInternet.visibility = View.GONE*/

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val currentView = currentFocus ?: window.decorView
            inputMethodManager?.hideSoftInputFromWindow(currentView.windowToken, 0)
        }

//      Работа с полем EditText
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                progressBar.visibility = progressBarVisibility(s)

                if (s?.isEmpty() == true) {
                    tracks.clear()
                    searchTrackList.notifyDataSetChanged()
                    placeholderMessageNothingFound.visibility = View.GONE
                    placeholderMessageNoInternet.visibility = View.GONE
                }

                /*if (s?.isEmpty() == true && searchHistory.getHistory().isNotEmpty()) {
                    searchHistory.historyTrackList.tracks = searchHistory.getHistory()
                    Log.d("Get", searchHistory.historyTrackList.tracks.toString())
                    history.visibility = View.VISIBLE
                }*/
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }
        queryInput.addTextChangedListener(simpleTextWatcher)

//      Обработчик "Скрытия клавиатуры"
        val rootLayout = findViewById<LinearLayout>(R.id.main)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfNeeded()
            }
            false
        }

//      Отображение песен
        searchTracksListRV.adapter = searchTrackList
        searchTracksListRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchTrackList.tracks = tracks

//      Кнопка "Поиск" на клавиатуре
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && queryInput.text.isNotEmpty() || actionId == EditorInfo.IME_ACTION_DONE && searchHistory.getHistory().isEmpty()) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                showTracks()
                true
            }
            hideKeyboardIfNeeded()
            false
        }

    }

}
package com.practicum.playlistmaker.search.ui.activity

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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.data.TimingInterceptor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import okhttp3.OkHttpClient
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by lazy {
        ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]
    }

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val STRING_DEF = ""
    }

    private lateinit var getTracksInteractor: TracksInteractor
    private lateinit var simpleTextWatcher: TextWatcher

    private lateinit var searchHistory: SearchHistory

    private var inputText = STRING_DEF
    private val tracks = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val client = OkHttpClient.Builder().addInterceptor(TimingInterceptor()).build()

    private val searchTrackList = TrackAdapter { track ->
        hideKeyboardIfNeeded()
        viewModel.saveTrack(track)
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

    //  Метод для поиска треков
    /*private fun searchRequest() {
        getTracksInteractor.searchTracks(
            text = queryInput.text.toString(),
            consumer = object : TracksInteractor.TracksConsumer{
                override fun consume(foundTracks: Resource<List<Track>>) {

                    val currentRunnable = searchRunnable
                    if (currentRunnable != null) {
                        handler.removeCallbacks(currentRunnable)
                    }

                    val newSearchRunnable = Runnable {
                        when (foundTracks) {
                            is Resource.Success -> {
                                if (foundTracks.data.isNotEmpty() && queryInput.text.toString().isNotEmpty()) {
                                    showSearchTracks(foundTracks.data)
                                } else if (queryInput.text.toString().isNotEmpty()) {
                                    showMessageNothingFound()
                                } else {
                                    showHistoryTracks()
                                }
                            }
                            else -> {
                                if (queryInput.text.toString().isNotEmpty()) {
                                    showMessageNoInternet()
                                } else {
                                    showHistoryTracks()
                                }
                            }
                        }
                    }

                    searchRunnable = newSearchRunnable
                    handler.post(newSearchRunnable)
                }

            }
        )
    }*/

    private fun render(state: TracksState) {
        when(state) {
            is TracksState.Content -> showSearchTracks(state.tracks)
            is TracksState.History -> showHistoryTracks(state.tracks)
            TracksState.Empty -> showMessageNothingFound()
            TracksState.Error -> showMessageNoInternet()
            TracksState.Loading -> showProgressBar()
        }
    }

    private fun showSearchTracks(tracks: List<Track>) {
        searchTrackList.tracks.clear()
        searchTrackList.tracks.addAll(tracks)
        searchTrackList.notifyDataSetChanged()

        binding.trackFound.isVisible = true
        binding.placeholderNoInternet.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.history.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showHistoryTracks(tracks: List<Track>) {
        searchHistory.historyTrackList.tracks.clear()
        searchHistory.historyTrackList.tracks.addAll(tracks)
        searchHistory.historyTrackList.notifyDataSetChanged()

        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = false
        binding.history.isVisible = tracks.isNotEmpty()
    }

    private fun showMessageNothingFound() {
        searchTrackList.tracks.clear()
        searchTrackList.notifyDataSetChanged()

        binding.placeholderNothingFound.isVisible = true
        binding.placeholderNoInternet.isVisible = false
        binding.history.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showMessageNoInternet() {
        searchTrackList.tracks.clear()
        searchTrackList.notifyDataSetChanged()

        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = true
        binding.history.isVisible = false
        binding.progressBar.isVisible = false
    }

    // Метод для обновления списка "Поиск"
    /*private fun update(changedText: String) {
        binding.updateBtn.setOnClickListener {
            viewModel.searchDebounce(changedText)
            hideKeyboardIfNeeded()
        }
    }*/

    // Метод для видимости "Прогресс бара"
    private fun showProgressBar() {
        binding.history.isVisible = false
        binding.trackFound.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = true
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

        val layoutManager = binding.trackFound.layoutManager as LinearLayoutManager
        outState.putInt("scroll_position", layoutManager.findFirstVisibleItemPosition())
        layoutManager.findViewByPosition(layoutManager.findFirstVisibleItemPosition())
            ?.let { view ->
                outState.putInt("scroll_offset", view.top - layoutManager.paddingTop)
            }

        outState.putInt("placeholder_nothing_found_visibility", binding.placeholderNothingFound.visibility)
        outState.putInt("placeholder_no_internet_visibility", binding.placeholderNoInternet.visibility)
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
        val layoutManager = binding.trackFound.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.history.isVisible = false
        binding.progressBar.isVisible = false

        getTracksInteractor = Creator.provideTracksInteractor(this)

        searchHistory = SearchHistory(this, binding.inputText, getTracksInteractor)
        binding.trackHistory.adapter = searchHistory.historyTrackList
        binding.trackHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.observerState().observe(this){
            render(it)
        }

//      Отобразить историю при запуске
        /*render(TracksState.History(viewModel.getTracksHistory()))*/

//      Очистить историю
        binding.clearHistory.setOnClickListener {
            getTracksInteractor.clearTracks()
            searchHistory.historyTrackList.notifyDataSetChanged()
            binding.history.isVisible = false
        }

        // Восстановление видимости
        savedInstanceState?.let {
            binding.placeholderNothingFound.visibility = it.getInt("placeholder_nothing_found_visibility", View.GONE)
            binding.placeholderNoInternet.visibility = it.getInt("placeholder_no_internet_visibility", View.GONE)
        }

//      Обработчик кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish()
        }

//      Обработчик кнопки "Очистить текст" в поиске
        binding.clearBtn.setOnClickListener {
            binding.inputText.setText("")
            binding.inputText.clearFocus()

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val currentView = currentFocus ?: window.decorView
            inputMethodManager?.hideSoftInputFromWindow(currentView.windowToken, 0)
        }

//      Работа с полем EditText
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearBtn.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(s.toString())

                if (s?.isEmpty() == true) {
                    tracks.clear()
                    searchTrackList.notifyDataSetChanged()
                    binding.placeholderNothingFound.isVisible = false
                    binding.placeholderNoInternet.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }
        binding.inputText.addTextChangedListener(simpleTextWatcher)
        binding.updateBtn.setOnClickListener{
            viewModel.updateList()
        }


//      Обработчик "Скрытия клавиатуры"
        val rootLayout = findViewById<LinearLayout>(R.id.main)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfNeeded()
            }
            false
        }

//      Отображение песен
        binding.trackFound.adapter = searchTrackList
        binding.trackFound.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchTrackList.tracks = tracks

//      Кнопка "Поиск" на клавиатуре
/*        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && queryInput.text.isNotEmpty() || actionId == EditorInfo.IME_ACTION_DONE && searchHistory.getHistory().isEmpty() getTracksInteractor.getTracks().isEmpty()) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                searchRequest()
                true
            }
            hideKeyboardIfNeeded()
            false
        }*/

    }

    override fun onDestroy() {
        val currentRunnable = searchRunnable
        if (currentRunnable != null) {
            handler.removeCallbacks(currentRunnable)
        }

        super.onDestroy()
    }

    private fun listToString(list: ArrayList<Track>): String {
        return list.joinToString(", ")
    }

}
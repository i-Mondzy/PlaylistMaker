package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var simpleTextWatcher: TextWatcher
    /*private val client = OkHttpClient.Builder().addInterceptor(TimingInterceptor()).build()*/

    private val searchTrackList = TrackAdapter { track ->
        hideKeyboard()
        viewModel.saveTrack(track)
        openPlayer(track)
    }

    private val historyTrackList = TrackAdapter { track ->
        hideKeyboard()
        viewModel.saveTrack(track)
        viewModel.updateHistory()
        openPlayer(track)
    }

    private fun openPlayer(track: Track) {
        startActivity(
            Intent(this, PlayerActivity::class.java).apply {
                putExtra("TRACK", track)
            }
        )
    }

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
        binding.trackFound.scrollToPosition(0)

        binding.trackFound.isVisible = true
        binding.history.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showHistoryTracks(tracks: List<Track>) {
        historyTrackList.tracks.clear()
        historyTrackList.tracks.addAll(tracks)
        historyTrackList.notifyDataSetChanged()
        binding.trackFound.scrollToPosition(0)

        binding.trackFound.isVisible = false
        binding.history.isVisible = tracks.isNotEmpty()
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showMessageNothingFound() {
        searchTrackList.tracks.clear()
        searchTrackList.notifyDataSetChanged()

        binding.trackFound.isVisible = false
        binding.history.isVisible = false
        binding.placeholderNothingFound.isVisible = true
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = false
    }

    private fun showMessageNoInternet() {
        searchTrackList.tracks.clear()
        searchTrackList.notifyDataSetChanged()

        binding.trackFound.isVisible = false
        binding.history.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = true
        binding.progressBar.isVisible = false
    }

    // Метод для видимости "Прогресс бара"
    private fun showProgressBar() {
        binding.history.isVisible = false
        binding.trackFound.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = true
    }

    //  Метод для "Скрытия клавиатуры"
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = currentFocus ?: window.decorView
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        currentFocus?.clearFocus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.history.isVisible = false
        binding.progressBar.isVisible = false

        // Список поиска
        binding.trackFound.adapter = searchTrackList
        binding.trackFound.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Список истории
        binding.trackHistory.adapter = historyTrackList
        binding.trackHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel.observerState().observe(this){
            render(it)
        }

//      Очистить историю
        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
            historyTrackList.notifyDataSetChanged()
            binding.history.isVisible = false
        }

//      Обработчик кнопки "Очистить текст" в поиске
        binding.clearBtn.setOnClickListener {
            binding.inputText.setText("")
            hideKeyboard()
        }

//      Обработчик кнопки "Обновить" в поиске
        binding.updateBtn.setOnClickListener{
            viewModel.updateSearch()
        }

//      Работа с полем EditText
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearBtn.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(s.toString())
            }

            override fun afterTextChanged(s: Editable?) { }
        }

        simpleTextWatcher.let { binding.inputText.addTextChangedListener(it) }

//      Обработчик кнопки "Назад"
        binding.btnBack.setOnClickListener {
            finish()
        }

//      Кнопка "Поиск" на клавиатуре
        binding.inputText.setOnEditorActionListener { _, _, _ ->
            hideKeyboard()
            false
        }

//      Обработчик нажатия по пустому месту на экране
        binding.root.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
            }
            false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher.let { binding.inputText.removeTextChangedListener(it) }
    }

}
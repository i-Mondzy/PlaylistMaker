package com.practicum.playlistmaker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Track
import com.practicum.playlistmaker.TrackAdapter
import com.practicum.playlistmaker.trackApi.TrackApi
import com.practicum.playlistmaker.trackApi.TracksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private lateinit var tracksList: RecyclerView
    private lateinit var updateBtn: Button

    private val adapter = TrackAdapter()
    private var tracks = ArrayList<Track>()

    private val baseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    private val trackService = retrofit.create(TrackApi::class.java)

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
        tracksList = findViewById(R.id.trackRecyclerView)
        updateBtn = findViewById(R.id.update_btn)

        placeholderMessageNothingFound.visibility = View.GONE
        placeholderMessageNoInternet.visibility = View.GONE

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

            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessageNothingFound.visibility = View.GONE
            placeholderMessageNoInternet.visibility = View.GONE

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val currentView = currentFocus ?: window.decorView
            inputMethodManager?.hideSoftInputFromWindow(currentView.windowToken, 0)
        }

//      Работа с полем EditText
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
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
        tracksList.adapter = adapter
        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter.tracks = tracks
        tracksList.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfNeeded()
            }
            false
        }

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // ВЫПОЛНЯЙТЕ ПОИСКОВЫЙ ЗАПРОС ЗДЕСЬ
                showTracks()
                hideKeyboardIfNeeded()
                true
            }
            false
        }

    }

//  Метод для отображения треков
    private fun showTracks() {
        trackService.search(queryInput.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()!!.results)
                                adapter.notifyDataSetChanged()
                                placeholderMessageNoInternet.visibility = View.GONE
                                placeholderMessageNothingFound.visibility = View.GONE
                                Log.d("Code", "Код: ${response.code()}")
                                Log.d("API_Response", "Ответ: ${response.body()}")
                                Log.d("Tracks", "Треков: ${tracks.size}")
                                Log.d("Time", "Время: ${tracks[1].trackTimeMillis}")
                            } else {
                                Log.d("Nothing200", "Ничего не найдено - ${response.code()}")
                                tracks.clear()
                                adapter.notifyDataSetChanged()
                                placeholderMessageNothingFound.visibility = View.VISIBLE
                                placeholderMessageNoInternet.visibility = View.GONE
                            }
                        }

                        else -> {
                            Log.d("Internet200", "Нет интернета - ${response.code()}")
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                            placeholderMessageNothingFound.visibility = View.GONE
                            placeholderMessageNoInternet.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    Log.d("InternetOnFailure", "Нет интернета - ${t.message.toString()}")
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    placeholderMessageNothingFound.visibility = View.GONE
                    placeholderMessageNoInternet.visibility = View.VISIBLE
                }

            })
    }

    private fun update() {
        updateBtn.setOnClickListener {
            showTracks()
            hideKeyboardIfNeeded()
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

        val layoutManager = tracksList.layoutManager as LinearLayoutManager
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
            adapter.notifyDataSetChanged()
        }

        val scrollPosition = savedInstanceState.getInt("scroll_position", 0)
        val scrollOffset = savedInstanceState.getInt("scroll_offset", 0)
        val layoutManager = tracksList.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(scrollPosition, scrollOffset)
    }

}
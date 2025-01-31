package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchActivity : AppCompatActivity() {
    var inputText = STRING_DEF
    private lateinit var networkStateReceiver: NetworkStateReceiver
    private lateinit var trackAdapter: TrackAdapter

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val STRING_DEF = ""
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

//      Обработчик кнопки "Назад"
        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener{
            finish()
        }

        val inputEditText = findViewById<EditText>(R.id.input_text) // Переменная для поля ввода EditText "Поиск"
        val clearButton = findViewById<ImageView>(R.id.clear_text) // Переменная для кнопки "Очистить" в поле ввода EditText

//      Обработчик кнопки "Очистить текст" в поиске
        clearButton.setOnClickListener{
            inputEditText.setText("")
            inputEditText.clearFocus()

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
        inputEditText.addTextChangedListener(simpleTextWatcher)

//      Обработчик "Скрытия клавиатуры"
        val rootLayout = findViewById<LinearLayout>(R.id.main)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfNeeded()
            }
            false
        }



//      Список песен
        var trackList = listOf(
            Track("Smells Like Teen Spirit", "Nirvana", "5:01","https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        )

//      Отображение песен
        trackAdapter = TrackAdapter(trackList)
        val recyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        recyclerView.adapter = trackAdapter
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfNeeded()
            }
            false
        }

//      Мониторинг состояния сети
        networkStateReceiver = NetworkStateReceiver(this) {
            runOnUiThread {
                updateImages()
            }
        }
        registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

//  Метод для обновления изображений
    private fun updateImages() {
        val updatedTrackList = trackAdapter.track.map { track ->
            track.copy(
                artworkUrl100 = track.artworkUrl100
            )
        }

        trackAdapter.updateTrackList(updatedTrackList)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkStateReceiver)
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

//  Метод для "Сохранения текста"
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, inputText)
    }

//  Метод для "Воостановления текста"
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(SEARCH_STRING, STRING_DEF)

        val editText = findViewById<EditText>(R.id.input_text)
        editText.setText(inputText)
    }

}
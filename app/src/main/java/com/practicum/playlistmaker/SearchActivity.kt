package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    var inputText = STRING_DEF

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val STRING_DEF = ""
    }

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
        val rootLayout = findViewById<FrameLayout>(R.id.main)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardIfNeeded()
            }
            false
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
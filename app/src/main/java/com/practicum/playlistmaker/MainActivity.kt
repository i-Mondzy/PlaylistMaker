package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val paddingHorizontal = resources.getDimensionPixelSize(R.dimen.default_padding)
            v.setPadding(systemBars.left + paddingHorizontal, systemBars.top,systemBars.right + paddingHorizontal, systemBars.bottom)
            insets
        }

        val searchButton = findViewById<Button>(R.id.search)
        searchButton.setOnClickListener{
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        val mediaButton = findViewById<Button>(R.id.media)
        mediaButton.setOnClickListener{
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        val settingsButton = findViewById<Button>(R.id.settings)
        settingsButton.setOnClickListener{
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
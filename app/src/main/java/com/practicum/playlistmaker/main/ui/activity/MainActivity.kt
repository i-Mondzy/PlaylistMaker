package com.practicum.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.MediaActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.main.ui.state.NavigationState
import com.practicum.playlistmaker.main.ui.view_model.MainViewModel
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top,systemBars.right, systemBars.bottom)
            insets
        }

        binding.search.setOnClickListener{
            viewModel.onSearchClick()
        }

        binding.media.setOnClickListener{
            viewModel.onMediaClick()
        }

        binding.settings.setOnClickListener{
            viewModel.onSettingsClick()
        }

        viewModel.observeNavigation().observe(this) {
            val intent = when(it) {
                NavigationState.Search -> Intent(this, SearchActivity::class.java)
                NavigationState.Media -> Intent(this, MediaActivity::class.java)
                NavigationState.Settings -> Intent(this, SettingsActivity::class.java)
            }

            startActivity(intent)
        }

    }
}
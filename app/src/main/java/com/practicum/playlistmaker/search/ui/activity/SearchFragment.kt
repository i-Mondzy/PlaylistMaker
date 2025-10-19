package com.practicum.playlistmaker.search.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.search.ui.compose_ui.SearchScreen
import org.koin.androidx.compose.koinViewModel

class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                SearchScreen(koinViewModel())
            }
        }
    }

}

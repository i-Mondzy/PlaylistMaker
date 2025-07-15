package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.state.TracksState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel by viewModel<SearchViewModel>()

    private var simpleTextWatcher: TextWatcher? = null
    /*private val client = OkHttpClient.Builder().addInterceptor(TimingInterceptor()).build()*/

    private var trackIndex: Int? = null

    private val searchTrackList = TrackAdapter { track, index ->
        hideKeyboard()
        trackIndex = index
        viewModel.saveTrack(track)
        openPlayer(track)
    }

    private val historyTrackList = TrackAdapter { track, index ->
        hideKeyboard()
        trackIndex = index
        viewModel.saveTrack(track)
        viewModel.updateHistory()
        openPlayer(track)
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment, PlayerFragment.createArgs(track))
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
        binding.trackFound.isVisible = false
        binding.history.isVisible = false
        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.progressBar.isVisible = true
    }

    //  Метод для "Скрытия клавиатуры"
    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = requireActivity().currentFocus ?: requireActivity().window.decorView
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        requireActivity().currentFocus?.clearFocus()
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeholderNothingFound.isVisible = false
        binding.placeholderNoInternet.isVisible = false
        binding.history.isVisible = false
        binding.progressBar.isVisible = false

        // Список поиска
        binding.trackFound.adapter = searchTrackList
        binding.trackFound.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Список истории
        binding.trackHistory.adapter = historyTrackList
        binding.trackHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.observerState().observe(viewLifecycleOwner){
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

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Boolean>("update")
            ?.observe(viewLifecycleOwner) { updated ->
                if (binding.trackFound.isVisible) {
                    trackIndex?.let { index ->
                        searchTrackList.tracks[index] = searchTrackList.tracks[index].copy(isFavorite = updated)
                    }
                } else if (binding.history.isVisible) {
                    trackIndex?.let { index ->
                        historyTrackList.tracks[0] = historyTrackList.tracks[0].copy(isFavorite = updated)
                    }
                }
                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Boolean>("update")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleTextWatcher.let { binding.inputText.removeTextChangedListener(it) }
        simpleTextWatcher = null
    }

}

package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteBinding
import com.practicum.playlistmaker.databinding.FragmentMediaBinding
import com.practicum.playlistmaker.media.ui.compose_ui.MediaScreen
import com.practicum.playlistmaker.utils.BindingFragment

class MediaFragment : Fragment() {

    private var tabMediator: TabLayoutMediator? = null
    private lateinit var binding: FragmentMediaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return ComposeView(requireContext()).apply {
            setContent {
                MediaScreen()
            }
        }
    }

/*    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaViewPagerAdapter(childFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

            when (position) {
                0 -> tab.text = getString(R.string.tabFavorite)
                1 -> tab.text = getString(R.string.tabPlaylists)
            }

        }
        tabMediator!!.attach()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator!!.detach()
        tabMediator = null
    }*/

}

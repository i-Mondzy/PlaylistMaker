package com.practicum.playlistmaker.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediaBinding
import com.practicum.playlistmaker.utils.BindingFragment

class MediaFragment : BindingFragment<FragmentMediaBinding>() {

    private var tabMediator: TabLayoutMediator? = null

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMediaBinding {
        return FragmentMediaBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
    }

}

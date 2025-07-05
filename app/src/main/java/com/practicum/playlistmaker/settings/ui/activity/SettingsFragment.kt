package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BindingFragment<FragmentSettingsBinding>() {

    private val viewModel by viewModel<SettingsViewModel>()

    private fun switchTheme(checked: Boolean) {
        binding.switchTheme.isChecked = checked
    }

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //      Переключатель темы "светлая-темная"
        viewModel.observeTheme().observe(viewLifecycleOwner) {
            switchTheme(it)
        }

        binding.switchTheme.setOnCheckedChangeListener{ switcher, checked ->
            viewModel.renderSwitchState(checked)
        }

//      Кнопка "Поделиться приложением"
        binding.share.setOnClickListener{
            viewModel.renderShareLink(getString(R.string.shareUrl))
        }

//      Кнопка "Написать в поддержку
        binding.support.setOnClickListener{
            viewModel.renderEmailSupport(
                getString(R.string.supportAddressMail),
                getString(R.string.supportTitleMail),
                getString(R.string.supportBodyMail)
            )
        }

//      Кнопа "Пользовательское соглашение"
        binding.userAgreement.setOnClickListener{
            viewModel.renderUserAgreement(getString(R.string.userAgreementUrl))
        }

    }

}

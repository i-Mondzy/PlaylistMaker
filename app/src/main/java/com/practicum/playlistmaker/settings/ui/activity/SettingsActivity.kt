package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//      Кнопка "Назад"
        binding.btnBack.setOnClickListener{
            finish()
        }

//      Переключатель темы "светлая-темная"
        viewModel.observeTheme().observe(this) {
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

    private fun switchTheme(checked: Boolean) {
        binding.switchTheme.isChecked = checked
    }

}

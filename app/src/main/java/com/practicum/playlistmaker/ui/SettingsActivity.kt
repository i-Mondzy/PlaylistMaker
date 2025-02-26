package com.practicum.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//      Кнопка "Назад"
        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener{
            finish()
        }

//      Обводка свитча при переключении
        val switch = findViewById<SwitchMaterial>(R.id.switch_theme)
        val sharedPrefs = getSharedPreferences(LIGHT_DARK_THEME, MODE_PRIVATE)
        switch.isChecked = sharedPrefs.getBoolean(THEME_KEY, false)
        switch.setOnCheckedChangeListener { switcher, checked ->
            if (!checked) {
                switch.background = ContextCompat.getDrawable(this, R.drawable.custom_ripple_night)
            } else {
                switch.background = ContextCompat.getDrawable(this, R.drawable.custom_ripple_day)
            }

            (applicationContext as App).switchTheme(checked)
        }

//      Кнопка "Поделиться приложением"
        val btnShare = findViewById<Button>(R.id.share)
        btnShare.setOnClickListener{
            val btnShareIntent = Intent(Intent.ACTION_SEND)
            btnShareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareUrl))
            btnShareIntent.type = "text/plain"
            startActivity(Intent.createChooser(btnShareIntent, "Поделиться через"))
        }

//      Кнопка "Написать в поддержку
        val btnSupport = findViewById<Button>(R.id.support)
        btnSupport.setOnClickListener{
            val btnSupportIntent = Intent(Intent.ACTION_SENDTO)
            btnSupportIntent.data = Uri.parse("mailto:")
            btnSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportAddressMail)))
            btnSupportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportTitleMail))
            btnSupportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.supportBodyMail))
            startActivity(btnSupportIntent)
        }

//      Кнопа "Пользовательское соглшанеие"
        val btnUserAgreement = findViewById<Button>(R.id.user_agreement)
        btnUserAgreement.setOnClickListener{
            val btnUserAgreementIntent = Intent(Intent.ACTION_VIEW)
            btnUserAgreementIntent.data = Uri.parse(getString(R.string.userAgreementUrl))
            startActivity(btnUserAgreementIntent)
        }

    }

}

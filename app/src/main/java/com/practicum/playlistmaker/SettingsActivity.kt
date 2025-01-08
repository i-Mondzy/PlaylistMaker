package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val btnBack = findViewById<Button>(R.id.btn_back)
        btnBack.setOnClickListener{
            finish()
        }



        val btnShare = findViewById<Button>(R.id.share)
        btnShare.setOnClickListener{
            val message = "https://practicum.yandex.ru/android-developer/"
            val btnShareIntent = Intent(Intent.ACTION_SEND)
            btnShareIntent.putExtra(Intent.EXTRA_TEXT, message)
            btnShareIntent.type = "text/plain"
            startActivity(Intent.createChooser(btnShareIntent, "Поделиться через"))
        }

        val btnSupport = findViewById<Button>(R.id.support)
        btnSupport.setOnClickListener{
            val titleMail = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val bodyMail = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val btnSupportIntent = Intent(Intent.ACTION_SENDTO)
            btnSupportIntent.data = Uri.parse("mailto:")
            btnSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("iMondzy@yandex.ru"))
            btnSupportIntent.putExtra(Intent.EXTRA_SUBJECT, titleMail)
            btnSupportIntent.putExtra(Intent.EXTRA_TEXT, bodyMail)
            startActivity(btnSupportIntent)
        }

        val btnUserAgreement = findViewById<Button>(R.id.user_agreement)
        btnUserAgreement.setOnClickListener{
            val url = "https://yandex.ru/legal/practicum_offer/"
            val btnUserAgreementIntent = Intent(Intent.ACTION_VIEW)
            btnUserAgreementIntent.data = Uri.parse(url)
            startActivity(btnUserAgreementIntent)
        }

    }

}

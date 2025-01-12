package com.practicum.playlistmaker

import android.content.Context
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
            val btnShareIntent = Intent(Intent.ACTION_SEND)
            btnShareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareUrl))
            btnShareIntent.type = "text/plain"
            startActivity(Intent.createChooser(btnShareIntent, "Поделиться через"))
        }

        val btnSupport = findViewById<Button>(R.id.support)
        btnSupport.setOnClickListener{
            val btnSupportIntent = Intent(Intent.ACTION_SENDTO)
            btnSupportIntent.data = Uri.parse("mailto:")
            btnSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportAddressMail)))
            btnSupportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.supportTitleMail))
            btnSupportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.supportBodyMail))
            startActivity(btnSupportIntent)
        }

        val btnUserAgreement = findViewById<Button>(R.id.user_agreement)
        btnUserAgreement.setOnClickListener{
            val btnUserAgreementIntent = Intent(Intent.ACTION_VIEW)
            btnUserAgreementIntent.data = Uri.parse(getString(R.string.userAgreementUrl))
            startActivity(btnUserAgreementIntent)
        }

    }

}

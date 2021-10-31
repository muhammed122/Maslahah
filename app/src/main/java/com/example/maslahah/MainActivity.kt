package com.example.maslahah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkUserLogin()
    }

    private fun checkUserLogin() {
        val login = MyPreference.getPrefBoolean("login")
        if (login) {
            startActivity(Intent(this, HomeActivityScreen::class.java))
            finish()
        }
    }
}
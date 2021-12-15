package com.example.maslahah

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.maslahah.ui.home.HomeActivityScreen
import com.example.maslahah.utils.MyPreference
import com.example.maslahah.utils.ProgressLoading

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ProgressLoading.init(this)
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
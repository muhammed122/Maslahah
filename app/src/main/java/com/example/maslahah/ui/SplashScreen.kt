package com.example.maslahah.ui

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Explode
import android.transition.Slide
import android.view.Window
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.example.maslahah.MainActivity
import com.example.maslahah.R
import kotlin.math.log

class SplashScreen : LocalizationActivity() {


    lateinit var logo: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        logo=findViewById(R.id.logo)


        logo.animate().alpha(1f).duration=1000


        Handler(Looper.myLooper()!!).run {
            postDelayed(kotlinx.coroutines.Runnable {
                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }

    }
}
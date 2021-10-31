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
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import com.example.maslahah.MainActivity
import com.example.maslahah.R

class SplashScreen : AppCompatActivity() {


    lateinit var motionLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.enterTransition = Explode()
        window.exitTransition = Slide()
        setContentView(R.layout.activity_splash_screen)
        motionLayout = findViewById(R.id.motion_layout)

        Handler(Looper.myLooper()!!).run {
            postDelayed(kotlinx.coroutines.Runnable {
                val intent = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this@SplashScreen).toBundle())
                finish()
            }, 3000)
        }

    }
}
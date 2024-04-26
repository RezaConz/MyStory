package com.example.mystory.view.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mystory.view.main.MainActivity
import com.example.mystory.view.onboarding.OnBoardingActivity
import com.example.mystory.R
import com.example.mystory.data.LoginPreferences
import com.example.mystory.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val content = findViewById<View>(android.R.id.content)
        @Suppress("UNUSED_EXPRESSION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnDrawListener { false }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val userLogin = runBlocking {
                LoginPreferences.getInstance(this@SplashActivity.dataStore).getLoginStatus().first()
            }
            if (userLogin == true){
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else{
                startActivity(Intent(applicationContext, OnBoardingActivity::class.java))
                finish()
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) }, 1500L)
    }
}
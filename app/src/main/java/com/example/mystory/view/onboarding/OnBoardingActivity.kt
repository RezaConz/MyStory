package com.example.mystory.view.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystory.R
import com.example.mystory.databinding.ActivityOnBoardingBinding
import com.example.mystory.view.welcome.WelcomeActivity

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .asGif()
            .load(R.drawable.gif_anim1)
            .into(binding.logoMenu1)
        Glide.with(this)
            .asGif()
            .load(R.drawable.gif_anim2)
            .into(binding.logoMenu2)
        Glide.with(this)
            .asGif()
            .load(R.drawable.gif_anim3)
            .into(binding.logoMenu3)
        Glide.with(this)
            .asGif()
            .load(R.drawable.gif_anim4)
            .into(binding.logoMenu4)


        binding.btnStart.setOnClickListener {
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }
        binding.btnSkip.setOnClickListener {
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }
    }
}
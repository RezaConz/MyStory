package com.example.mystory.view.detail

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mystory.R
import com.example.mystory.data.remote.response.ListStoryItem
import com.example.mystory.databinding.ActivityDetailBinding
import com.example.mystory.util.DateFormat
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val receivedData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_DATA, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_DATA)
        }

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(receivedData?.photoUrl)
                .error(R.drawable.shiba_vector)
                .into(ivDetailPhoto)
            val datePosted = DateFormat.formatDate(receivedData?.createdAt.toString(), TimeZone.getDefault().id)
            tvDetailTime.text = getString(R.string.created_at, datePosted)
            tvDetailName.text = receivedData?.name
            tvDetailDescription.text = receivedData?.description
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_DATA: String = "extra_data"
    }
}
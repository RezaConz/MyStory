package com.example.mystory.view.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.R
import com.example.mystory.data.LanguagePreferences
import com.example.mystory.databinding.ActivitySettingBinding
import com.example.mystory.view.SettingViewModelFactory
import com.example.mystory.view.main.MainActivity
import java.util.Locale

private val Context.dataStoreLanguage: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var locale: Locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val preferences = LanguagePreferences.getInstance(dataStoreLanguage)
        val settingsViewModel = ViewModelProvider(this, SettingViewModelFactory(preferences))[SettingViewModel::class.java]

        settingsViewModel.getCurrentLanguage().observe(this) { language ->
            locale = language
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
        }

        binding.btnEnglish.setOnClickListener {
            locale = Locale("en", "US")
            settingsViewModel.setCurrentLanguage("en")
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
            Toast.makeText(this, R.string.switch_language, Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        binding.btnIndo.setOnClickListener {
            locale = Locale("in", "ID")
            settingsViewModel.setCurrentLanguage("in")
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
            Toast.makeText(this, R.string.switch_language, Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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
}
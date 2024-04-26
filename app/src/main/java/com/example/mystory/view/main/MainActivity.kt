package com.example.mystory.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystory.R
import com.example.mystory.adapter.LoadingAdapter
import com.example.mystory.adapter.StoryAdapter
import com.example.mystory.data.LoginPreferences
import com.example.mystory.data.dataStore
import com.example.mystory.databinding.ActivityMainBinding
import com.example.mystory.view.ViewModelFactory
import com.example.mystory.view.welcome.WelcomeActivity
import com.example.mystory.data.Result
import com.example.mystory.view.addstory.AddStoryActivity
import com.example.mystory.view.detail.DetailActivity
import com.example.mystory.view.map.MapsActivity
import com.example.mystory.view.setting.SettingActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.show()

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(
                this,
                LoginPreferences.getInstance(dataStore)
            )
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupStoryList()
        binding.swipeToRefresh.setOnRefreshListener {
            setupStoryList()
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.are_your_sure))
                    setPositiveButton(getString(R.string.continue_dialog)) { _, _ ->
                        viewModel.logout()
                        val intent = Intent(context,WelcomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    create()
                    show()
                }
                true
            }
            R.id.action_map -> {
                val intent = Intent(this,MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_setting -> {
                val intent = Intent(this,SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }

    private fun setupStoryList(){
        val storyListAdapter = StoryAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyListAdapter
            storyListAdapter.withLoadStateFooter(
                footer = LoadingAdapter {
                    storyListAdapter.retry()
                }
            )
        }

        storyListAdapter.onClick = {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_DATA, it)
            startActivity(intent)
        }

        viewModel.getStories.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val storyData = result.data
                        storyListAdapter.submitData(lifecycle,storyData)
                        binding.swipeToRefresh.isRefreshing = false
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            getString(R.string.error) + result.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
package com.example.mystory.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mystory.R
import com.example.mystory.data.LoginPreferences
import com.example.mystory.databinding.ActivityRegisterBinding
import com.example.mystory.view.ViewModelFactory
import com.example.mystory.view.login.LoginActivity
import com.example.mystory.view.welcome.WelcomeActivity
import com.example.mystory.data.Result
import com.example.mystory.data.dataStore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.GONE

        val factory: ViewModelFactory =
            ViewModelFactory.getInstance(
                this,
                LoginPreferences.getInstance(dataStore)
            )
        val viewModel: RegisterViewModel = ViewModelProvider(this,factory)[RegisterViewModel::class.java]

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            viewModel.register(name, email, password).observe(this@RegisterActivity){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val response = result.data
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.success))
                                setMessage(response.message)
                                setPositiveButton(getString(R.string.continue_dialog)) { _, _ ->
                                    startActivity(Intent(this@RegisterActivity, WelcomeActivity::class.java))
                                }
                                create()
                                show()
                            }.apply {
                                setOnCancelListener { // Set an OnCancelListener to handle the case when the user clicks outside of the dialog
                                    startActivity(Intent(this@RegisterActivity, WelcomeActivity::class.java))
                                }
                                show()
                            }
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.error))
                                setMessage(result.error)
                                setPositiveButton(getString(R.string.continue_dialog)) { _, _ -> }
                                create()
                                show()
                            }
                        }
                    }
                }
            }

        }

    }
}
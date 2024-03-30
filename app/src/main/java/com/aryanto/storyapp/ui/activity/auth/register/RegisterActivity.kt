package com.aryanto.storyapp.ui.activity.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aryanto.storyapp.R
import com.aryanto.storyapp.databinding.ActivityRegisterBinding
import com.aryanto.storyapp.ui.activity.auth.login.LoginActivity
import com.aryanto.storyapp.ui.utils.ClientState
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerVM: RegisterVM by viewModel<RegisterVM>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setView()
        setRegisterBtn()
        setPageLogin()

    }

    private fun setView() {
        binding.apply {
            registerVM.register.observe(this@RegisterActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        progressBarRegister.visibility = View.GONE
                        handleSuccess()
                    }

                    is ClientState.Error -> {
                        progressBarRegister.visibility = View.GONE
                        handleError(resources.message)
                    }

                    is ClientState.Loading -> {
                        progressBarRegister.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleError(errorMSG: String?) {
        binding.apply {
            when {
                errorMSG?.contains("name") == true -> {
                    nameTiLayout.error = errorMSG
                }

                errorMSG?.contains("email") == true -> {
                    emailTiLayout.error = errorMSG
                }

                errorMSG?.contains("password") == true -> {
                    passwordTiLayout.error = errorMSG
                }

                else -> {
                    showToast("$errorMSG")
                }
            }
        }
    }

    private fun setRegisterBtn() {
        binding.apply {
            btnSubmitRegister.setOnClickListener {
                val name = nameEdtRegister.text.toString()
                val email = emailEdtRegister.text.toString()
                val pass = passwordEdtRegister.text.toString()
                registerVM.performRegister(name, email, pass)
            }
        }
    }

    private fun setPageLogin() {
        binding.apply {
            tvLoginHere.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
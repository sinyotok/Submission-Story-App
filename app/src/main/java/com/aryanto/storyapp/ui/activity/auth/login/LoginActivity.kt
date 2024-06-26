package com.aryanto.storyapp.ui.activity.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.aryanto.storyapp.R
import com.aryanto.storyapp.databinding.ActivityLoginBinding
import com.aryanto.storyapp.ui.activity.auth.register.RegisterActivity
import com.aryanto.storyapp.ui.activity.home.HomeActivity
import com.aryanto.storyapp.ui.core.data.model.LoginResult
import com.aryanto.storyapp.ui.core.data.remote.network.ApiClient
import com.aryanto.storyapp.ui.utils.ClientState
import com.aryanto.storyapp.ui.utils.TokenManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginVM: LoginVM by viewModel<LoginVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLoginBtn()
        setView()
        setPageRegister()
        applyAnimation()

    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        lifecycleScope.launch {
            val tokenManager = TokenManager.getInstance(this@LoginActivity)
            val (token, session) = tokenManager.getTokenAndSession()
            if (token != null && session == true) {
                ApiClient.setAuthToken(token)
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setLoginBtn() {
        binding.apply {
            btnSubmitLogin.setOnClickListener {
                val email = emailEdtLogin.getEmail()
                val pass = passwordEdtLogin.getPass()
                loginVM.performLogin(email, pass)
            }
        }
    }

    private fun setView() {
        binding.apply {
            loginVM.loginResult.observe(this@LoginActivity) { resources ->
                when (resources) {
                    is ClientState.Success -> {
                        progressBarLogin.visibility = View.GONE
                        resources.data?.let { handleLoginSuccess(it) }
                    }

                    is ClientState.Error -> {
                        handleError(resources.message)
                        progressBarLogin.visibility = View.GONE
                    }

                    is ClientState.Loading -> {
                        progressBarLogin.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handleLoginSuccess(loginResult: LoginResult) {
        lifecycleScope.launch {
            val auth = loginResult.token
            val tokenManager = TokenManager.getInstance(this@LoginActivity)

            tokenManager.saveTokenAndSession(auth, true)

            val (token) = tokenManager.getTokenAndSession()
            if (token != null) {
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showToast("Session has expired")
            }

        }
    }

    private fun handleError(errorMSG: String?) {
        binding.apply {
            when {
                errorMSG?.contains("email") == true -> {
                    emailEdtLogin.setEmailError(errorMSG)
                }

                errorMSG?.contains("password") == true -> {
                    passwordEdtLogin.setPassError(errorMSG)
                }

                else -> {
                    showToast("$errorMSG")
                }
            }
        }
    }

    private fun setPageRegister() {
        binding.apply {
            tvRegisterHere.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun applyAnimation() {
        binding.apply {
            val imgView = ivLogin
            val tv1 = tv1Login
            val tv2 = tv2Login
            val email = emailEdtLogin
            val pass = passwordEdtLogin
            val btnLogin = btnSubmitLogin
            val tv3 = tv3Login
            val tvRegister = tvRegisterHere

            val anim1 = ObjectAnimator.ofFloat(imgView, View.TRANSLATION_Y, -600f, 0f)
            val anim2 = ObjectAnimator.ofFloat(tv1, View.TRANSLATION_Y, -600f, 0f)
            val anim3 = ObjectAnimator.ofFloat(tv2, View.TRANSLATION_Y, -600f, 0f)
            val anim4 = ObjectAnimator.ofFloat(email, View.TRANSLATION_Y, -600f, 0f)
            val anim5 = ObjectAnimator.ofFloat(pass, View.TRANSLATION_Y, -600f, 0f)
            val anim6 = ObjectAnimator.ofFloat(btnLogin, View.TRANSLATION_Y, -600f, 0f)
            val anim7 = ObjectAnimator.ofFloat(tv3, View.TRANSLATION_Y, -600f, 0f)
            val anim8 = ObjectAnimator.ofFloat(tvRegister, View.TRANSLATION_Y, -600f, 0f)

            val duration = 3000L
            val interpolator = DecelerateInterpolator()

            anim1.duration = duration
            anim1.interpolator = interpolator

            anim2.duration = duration
            anim2.interpolator = interpolator

            anim3.duration = duration
            anim3.interpolator = interpolator

            anim4.duration = duration
            anim4.interpolator = interpolator

            anim5.duration = duration
            anim5.interpolator = interpolator

            anim6.duration = duration
            anim6.interpolator = interpolator

            anim7.duration = duration
            anim7.interpolator = interpolator

            anim8.duration = duration
            anim8.interpolator = interpolator

            val set = AnimatorSet()
            set.playTogether(
                anim1, anim2, anim3, anim4, anim5, anim6, anim7, anim8
            )
            set.start()

        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }


}
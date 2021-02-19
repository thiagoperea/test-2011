package com.thiagoperea.mybills.presentation.screens.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thiagoperea.mybills.core.extension.showToast
import com.thiagoperea.mybills.presentation.databinding.ActivitySplashBinding
import com.thiagoperea.mybills.presentation.screens.main.MainActivity
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_LOGIN = 1
    }

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        viewModel.verifyUserLogged()
    }

    private fun setupObservers() {
        viewModel.splashState.observe(this) { state ->
            when (state) {
                SplashState.LoginValid -> goToMainScreen()
                SplashState.LoginInvalid -> goToLoginScreen()
            }
        }
    }

    private fun goToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLoginScreen() {
        val intent = viewModel.createLoginIntent()
        startActivityForResult(intent, REQUEST_CODE_LOGIN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                goToMainScreen()
            } else if (data != null) {
                showToast("Deu erro no cadastro :(")
            }
        }
    }
}
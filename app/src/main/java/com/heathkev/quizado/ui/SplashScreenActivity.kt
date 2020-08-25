package com.heathkev.quizado.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.heathkev.quizado.result.EventObserver
import com.heathkev.quizado.ui.LaunchDestination.LOGIN
import com.heathkev.quizado.ui.LaunchDestination.MAIN_ACTIVITY
import com.heathkev.quizado.utils.checkAllMatched
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SplashScreenActivityViewModel by viewModels()

        viewModel.launchDestination.observe(this, EventObserver { destination ->
            when (destination) {
                MAIN_ACTIVITY-> startActivity(Intent(this, MainActivity::class.java))
                LOGIN -> startActivity(Intent(this, LoginActivity::class.java))
            }.checkAllMatched
            finish()
        })
    }
}
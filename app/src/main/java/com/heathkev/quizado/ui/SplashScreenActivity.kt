package com.heathkev.quizado.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.heathkev.quizado.utils.checkAllMatched
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreenActivityViewModel: SplashScreenActivityViewModel by viewModels()

        splashScreenActivityViewModel.launchDestination.observe(this, Observer { destination ->
            when(destination){
                LaunchDestination.MAIN_ACTIVITY -> startActivity(Intent(this, MainActivity::class.java))
                LaunchDestination.LOGIN -> startActivity(Intent(this, LoginActivity::class.java))
            }.checkAllMatched
            finish()
        })
    }
}
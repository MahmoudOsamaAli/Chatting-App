package com.example.chatbox.startup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.chatbox.databinding.ActivitySplashBinding
import com.example.chatbox.main.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)
        // Declare Binding to Access all the views
        binding = ActivitySplashBinding.inflate(layoutInflater)

        val currentUser = Firebase.auth.currentUser
        MainScope().launch {
            delay(2000)
            if (currentUser == null) {
                startActivity(Intent(this@SplashActivity, StartupActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()

        }
    }
}

package com.example.chatbox.startup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import com.example.chatbox.auth.signup.SignupActivity
import com.example.chatbox.databinding.ActivityStartupBinding


class StartupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartupBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartupBinding.inflate(layoutInflater)
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightStatusBars = false
        installSplashScreen()
        actionBar?.hide()
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
    }
}




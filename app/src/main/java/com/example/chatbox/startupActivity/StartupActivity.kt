package com.example.chatbox.startupActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityStartupBinding
import com.example.chatbox.login.LoginActivity
import com.example.chatbox.signup.SignupActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { true }

        super.onCreate(savedInstanceState)

        MainScope().launch {
            delay(1500)
            splash.setKeepOnScreenCondition { false }
        }
        enableEdgeToEdge()
        // Declare Binding to Access all the views
        binding = DataBindingUtil.setContentView(this, R.layout.activity_startup)
        supportActionBar?.hide()
        // Switching from startup Activity to Login Activity
        binding.LogIn.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
        // switching to signup Activity
        binding.BtnSignUp.setOnClickListener { startActivity(Intent(this, SignupActivity::class.java)) }

    }


}
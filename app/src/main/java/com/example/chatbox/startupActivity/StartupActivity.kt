package com.example.chatbox.startupActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.chatbox.login.LoginActivity
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityStartupBinding

class StartupActivity : AppCompatActivity() {
    lateinit var binding: ActivityStartupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Declare Binding to Access all the views
        binding = ActivityStartupBinding.inflate(layoutInflater)

        // Handle the splash screen transition.
        installSplashScreen()

        // hide ActionBar
        actionBar?.hide()

        setContentView(binding.root)

        // Switching from startup Activity to Login Activity
        binding.LogIn.setOnClickListener { switchingToLonginActivity() }
    }

    private fun switchingToLonginActivity() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

}
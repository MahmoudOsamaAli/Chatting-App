package com.example.chatbox.startupActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import com.example.chatbox.login.LoginActivity
import com.example.chatbox.signup.SignupActivity
import com.example.chatbox.databinding.ActivityStartupBinding

class StartupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartupBinding
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Declare Binding to Access all the views
        binding = ActivityStartupBinding.inflate(layoutInflater)
        // Change the StatusBar Title color to White
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightStatusBars = false
        // Handle the splash screen transition.
        installSplashScreen()
        // hide ActionBar
        actionBar?.hide()
        setContentView(binding.root)
        // Switching from startup Activity to Login Activity
        binding.LogIn.setOnClickListener { switchingToLonginActivity() }
        // switching to signup Activity
        binding.BtnSignUp.setOnClickListener { swapToSignupActivity() }

    }

    private fun swapToSignupActivity() {
        val intent = Intent(this,SignupActivity::class.java)
        startActivity(intent)
    }

    private fun switchingToLonginActivity() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

}
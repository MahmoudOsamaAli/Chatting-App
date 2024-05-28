package com.example.chatbox.startupActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Switching from startup Activity to Login Activity
        binding.LogIn.setOnClickListener { switchingToLonginActivity() }


    }

    private fun switchingToLonginActivity() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

}
package com.example.chatbox.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbox.home.MainActivity
import com.example.chatbox.startupActivity.StartupActivity
import com.example.chatbox.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // change colors of status bar and title
        setStatusBar()
        // backArrow to startup Activity
        binding.icBackArrow.setOnClickListener { backToStartupActivity() }
        //swap to home Activity
        binding.btnLogin.setOnClickListener {  swapToMainActivity()}
    }

    private fun swapToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun backToStartupActivity() {
        val intent = Intent(this,StartupActivity::class.java)
        startActivity(intent)
    }

    private fun setStatusBar() {
        // to make the status bar transparent
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.BLACK
        // to change status bar title color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
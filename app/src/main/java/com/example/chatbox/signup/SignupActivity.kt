package com.example.chatbox.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbox.databinding.ActivitySignupBinding
import com.example.chatbox.startupActivity.StartupActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.icBackArrow.setOnClickListener { backToStartupActivity() }
    }

    private fun backToStartupActivity() {
        val intent = Intent(this, StartupActivity::class.java)
        startActivity(intent)
    }

}
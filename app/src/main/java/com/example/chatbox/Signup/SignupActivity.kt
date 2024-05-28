package com.example.chatbox.Signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatbox.R
import com.example.chatbox.StartupActivity.StartupActivity
import com.example.chatbox.databinding.ActivitySignupBinding
import com.example.chatbox.databinding.ActivityStartupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.icBackArrow.setOnClickListener { backToStartupActivity() }
    }

    private fun backToStartupActivity() {
        val intent = Intent(this,StartupActivity::class.java)
        startActivity(intent)
    }

}
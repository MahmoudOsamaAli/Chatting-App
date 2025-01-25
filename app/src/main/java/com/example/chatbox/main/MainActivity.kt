package com.example.chatbox.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityMainBinding
import com.example.chatbox.main.fragments.home.Chats.ChatMessages.ChatFragment



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleNavController()
        hideBottomNavForChat()

    }

    private fun handleNavController() {
        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.fragments_container) as NavHostFragment
        val navController = navigationHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
    private fun hideBottomNavForChat() {
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragments_container)
            if (currentFragment is ChatFragment) {
                // to hide the bottom Navigation
                binding.bottomNavigationView.visibility = View.GONE
                // to handle the status bar color and title color
                window.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    statusBarColor = Color.WHITE
                    navigationBarColor = Color.BLACK
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
                window.apply {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    statusBarColor = Color.TRANSPARENT
                    navigationBarColor = Color.BLACK
                    decorView.systemUiVisibility = 0
                }
            }
        }
    }


}

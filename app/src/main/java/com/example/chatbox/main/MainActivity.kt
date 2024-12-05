package com.example.chatbox.main

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityMainBinding
import com.example.chatbox.main.fragments.calls.CallsFragment
import com.example.chatbox.main.fragments.contacts.ContactsFragment
import com.example.chatbox.main.fragments.home.ChatFragment
import com.example.chatbox.main.fragments.home.HomeFragment
import com.example.chatbox.main.fragments.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        hideBottomNavForChat()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
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

    // Handles bottom navigation selection and switches between fragments
    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.message_item -> HomeFragment()
                R.id.calls_item -> CallsFragment()
                R.id.contacts_item -> ContactsFragment()
                R.id.settings_item -> SettingsFragment()
                else -> return@setOnItemSelectedListener false
            }
            loadFragment(selectedFragment)
            true
        }
    }

    // Loads the specified fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container, fragment) // Replace the fragment container's content
            .commit()

    }
}

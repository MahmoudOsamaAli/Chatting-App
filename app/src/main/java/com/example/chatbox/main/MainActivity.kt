package com.example.chatbox.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityMainBinding
import com.example.chatbox.main.fragments.calls.CallsFragment
import com.example.chatbox.main.fragments.contacts.ContactsFragment
import com.example.chatbox.main.fragments.home.MessageFragment
import com.example.chatbox.main.fragments.settings.SettingsFragment

// MainActivity is the entry point of the application handling UI and fragment switching
class MainActivity : AppCompatActivity() {

    // Using ViewBinding to reference views
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the status bar appearance
        setupStatusBar()

        // Initialize bottom navigation menu
        setupBottomNavigation()

        // Load the default fragment (MessageFragment) when activity starts
        if (savedInstanceState == null) {
            loadFragment(MessageFragment())
        }
    }

    // Changes the status bar appearance to set title color to white
    private fun setupStatusBar() {
        // This function makes the status bar icons appear light (for dark backgrounds)
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = false
    }

    // Handles bottom navigation selection and switches between fragments
    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.message_item -> MessageFragment() // Loads MessageFragment on message tab click
                R.id.calls_item -> CallsFragment()             // Loads Calls fragment on call tab click
                R.id.contacts_item -> ContactsFragment()       // Loads Contacts fragment on contacts tab click
                R.id.settings_item -> SettingsFragment()       // Loads Settings fragment on settings tab click
                else -> return@setOnItemSelectedListener false
            }
            loadFragment(selectedFragment) // Replace current fragment with selected one
            true
        }
    }

    // Loads the specified fragment into the fragment container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container, fragment) // Replace the fragment container's content
            .commit() // Commit the transaction
    }
}

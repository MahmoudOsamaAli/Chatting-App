package com.example.chatbox.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityMainBinding
import com.example.chatbox.home.fragments.Calls
import com.example.chatbox.home.fragments.Contacts
import com.example.chatbox.home.fragments.Message
import com.example.chatbox.home.fragments.Settings

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private val message = Message()
    private val calls = Calls()
    private val contacts = Contacts()
    private val settings = Settings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding to Access all the views
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Change the StatusBar Title color to White
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightStatusBars = false

        setContentView(binding.root)

        // to handle swapping between bottom navigation fragments
        addNavigationListener()
    }
    private fun addNavigationListener() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId)
            {
                R.id.message_item ->
                {
                    fragmentTransaction(message)
                    true
                }
                R.id.calls_item ->
                {
                    fragmentTransaction(calls)
                    true
                }
                R.id.contacts_item ->
                {
                    fragmentTransaction(contacts)
                    true
                }
                R.id.settings_item ->
                {
                    fragmentTransaction(settings)
                    true
                }
                else -> false
            }

        }
    }

    private fun fragmentTransaction(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragments_container, fragment)
            commit()
        }
    }
}
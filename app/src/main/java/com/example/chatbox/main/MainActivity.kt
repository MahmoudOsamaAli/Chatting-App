package com.example.chatbox.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityMainBinding
import com.example.chatbox.repository.FireBaseAuthRepo
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dataBase = FirebaseDatabase.getInstance()
    private val userId = FireBaseAuthRepo.getUserUUID()

    private val userStateRef = dataBase.getReference("users").child(userId!!)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val formattedTime = timeFormat.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userStateRef.updateChildren(mapOf("userState" to "Online"))
        handleNavController()
    }
    override fun onStop() {
        super.onStop()
        userStateRef.child("userState").onDisconnect().setValue(formattedTime)
    }
    private fun handleNavController() {
        val navigationHost =
            supportFragmentManager.findFragmentById(R.id.fragments_container) as NavHostFragment
        val navController = navigationHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}

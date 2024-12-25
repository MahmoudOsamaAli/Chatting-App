package com.example.chatbox.auth.startupActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbox.auth.GoogleAuthHelper
import com.example.chatbox.auth.login.LoginActivity
import com.example.chatbox.auth.signup.SignupActivity
import com.example.chatbox.databinding.ActivityStartupBinding
import com.example.chatbox.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class StartupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartupBinding
    private lateinit var googleAuthHelper: GoogleAuthHelper

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        googleAuthHelper = GoogleAuthHelper(this)

        // Google Sign-In button listener
        binding.googleLogin.setOnClickListener {
            googleAuthHelper.startGoogleSignIn(googleSignInLauncher)
        }

        // Check user status
        checkUserStatus()

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.BtnSignUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    // Register for Google Sign-In result
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            googleAuthHelper.handleSignInResult(
                result.resultCode,
                result.data,
                onSuccess = { idToken ->
                    googleAuthHelper.signInWithFirebase(idToken,
                        onSuccess = { user ->
                            Log.d("Firebase Sign-In", "Successfully signed in with Firebase: ${user?.email}")
                            navigateToMain(user)
                        },
                        onFailure = { error ->
                            Log.e("Firebase Sign-In", error)
                        })
                },
                onFailure = { error ->
                    Log.e("Google Sign-In", error)
                }
            )
        }

    private fun checkUserStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navigateToMain(currentUser)
        }
    }

    private fun navigateToMain(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

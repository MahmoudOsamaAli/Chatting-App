//package com.example.chatbox.auth.startupActivity
//
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.core.view.ViewCompat
//import com.example.chatbox.R
//import com.example.chatbox.databinding.ActivityStartupBinding
//import com.example.chatbox.main.MainActivity
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//@Suppress("DEPRECATION")
//class StartupActivity : AppCompatActivity() {
//
//    companion object {
//        private const val TAG = "StartupActivity"
//    }
//    private val RC_SIGN_IN = 2
//    private lateinit var binding: ActivityStartupBinding
//    private lateinit var auth: FirebaseAuth
//
//    @RequiresApi(Build.VERSION_CODES.R)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityStartupBinding.inflate(layoutInflater)
//        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
//        windowInsetController?.isAppearanceLightStatusBars = false
//        installSplashScreen()
//        actionBar?.hide()
//        setContentView(binding.root)
//        auth = Firebase.auth
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        binding.btnLogin.setOnClickListener {
//            val signInIntent = mGoogleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
////            val signInRequest = BeginSignInRequest.builder()
////                .setGoogleIdTokenRequestOptions(
////                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
////                        .setSupported(true)
////                        .setServerClientId(getString(R.string.default_web_client_id))
//////                        .setFilterByAuthorizedAccounts(fal)
////                        .build()
////                ).build()
////
////            val oneTapClient = Identity.getSignInClient(this)
////            oneTapClient.beginSignIn(signInRequest)
////                .addOnSuccessListener { result ->
////                    try {
////                        val intentSender = result.pendingIntent.intentSender
////                        startIntentSenderForResult(intentSender, REQ_ONE_TAP, null, 0, 0, 0)
////                    } catch (e: ApiException) {
////                        Log.e(TAG, "Failed to begin sign in: ${e.localizedMessage}")
////                    }
////                }
////                .addOnFailureListener { e ->
////                    Log.e(TAG, "Failed to begin sign in: ${e.localizedMessage}")
////                }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        when (requestCode) {
//            RC_SIGN_IN -> {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//                try {
//                    // Google Sign In was successful, authenticate with Firebase
//                    val account = task.getResult(ApiException::class.java)
//                    firebaseAuthWithGoogle(account.idToken)
//                } catch (e: ApiException) {
//                    // Google Sign In failed, update UI appropriately
//                }
////                try {
////                    val credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data)
////                    val idToken = credential.googleIdToken
////                    when {
////                        idToken != null -> {
////                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
////                            auth.signInWithCredential(firebaseCredential)
////                                .addOnCompleteListener(this) { task ->
////                                    if (task.isSuccessful) {
////                                        Log.d(TAG, "signInWithCredential:success")
////                                        val user = auth.currentUser
////                                        continueWithTheFlow()
////                                    } else {
////                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
////                                    }
////                                }
////                        }
////                        else -> {
////                            Log.d(TAG, "No ID token!")
////                        }
////                    }
////                } catch (e: ApiException) {
////                    Log.e(TAG, "Error: ${e.localizedMessage}")
////                }
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String?) {
//        val credential = GoogleAuthProvider.getCredential(idToken, /*accessToken=*/ null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    val user = auth.currentUser
//                    continueWithTheFlow()
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    private fun continueWithTheFlow() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }
//}

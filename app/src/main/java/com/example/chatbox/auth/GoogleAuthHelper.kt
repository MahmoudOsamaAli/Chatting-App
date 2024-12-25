package com.example.chatbox.auth

import android.app.Activity
import android.content.Intent
import com.example.chatbox.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthHelper(private val activity: Activity) {

    private val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(activity)
    }

    private val signInRequest: BeginSignInRequest by lazy {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false) // Show all accounts, not just authorized ones
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun startGoogleSignIn(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    activity.startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        RC_SIGN_IN,
                        null,
                        0,
                        0,
                        0,
                        null
                    )
                } catch (e: Exception) {
                    onFailure("Failed to start Google Sign-In: ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Google Sign-In initiation failed: ${exception.message}")
            }
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        oneTapClient.getSignInCredentialFromIntent(data)?.let { credential ->
            val idToken = credential.googleIdToken
            if (idToken != null) {
                onSuccess(idToken)
            } else {
                onFailure("No ID token found")
            }
        } ?: run {
            onFailure("Sign-In result is null or invalid")
        }
    }

    fun signInWithFirebase(
        idToken: String,
        onSuccess: (FirebaseUser?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                onSuccess(user)
            } else {
                onFailure("Firebase sign-in failed: ${task.exception?.message}")
            }
        }
    }

    fun checkUserStatus(onUserFound: (FirebaseUser) -> Unit, onUserNotFound: () -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            onUserFound(currentUser)
        } else {
            onUserNotFound()
        }
    }

    companion object {
        const val RC_SIGN_IN = 1001
    }
}

package com.example.chatbox.repository

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.example.chatbox.R
import com.example.chatbox.data.response.AuthResponse
import com.example.chatbox.network.ServerCallBack
import com.example.chatbox.utils.Constants
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

object FireBaseAuthRepo {

    private fun getFireBaseAuth() = Firebase.auth

    fun getUserPhoneNumber() = getFireBaseAuth().currentUser?.phoneNumber

    fun getUserUUID() = getFireBaseAuth().currentUser?.uid

    fun authorizePhoneNumber(
        phoneNumber: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null,
        context: Activity, callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(getFireBaseAuth())
            .setPhoneNumber(phoneNumber)
            .setTimeout(Constants.CALL_TIMEOUT, TimeUnit.SECONDS) // Timeout for the code sent via SMS
            .setActivity(context) // The activity to which the user is navigated to enter the code
            .setCallbacks(callbacks) // Implement PhoneAuthProvider.OnVerificationStateChangedCallbacks
        if (resendToken != null) {
            options.setForceResendingToken(resendToken)
        }
        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    fun signInUserWithPhoneNumber(credential: PhoneAuthCredential, activity: Activity): MutableLiveData<ServerCallBack<AuthResponse>> {
        val liveData = MutableLiveData<ServerCallBack<AuthResponse>>()
        liveData.value = ServerCallBack.loading()
        getFireBaseAuth().signInWithCredential(credential).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                liveData.value = ServerCallBack.success(data = AuthResponse(true, activity.getString(R.string.authenticated_successfully)))
            } else {
                liveData.value = ServerCallBack.error(data = AuthResponse(false, activity.getString(R.string.authenticated_failed)), message = activity.getString(R.string.authenticated_failed))
            }
        }
        return liveData
    }
}


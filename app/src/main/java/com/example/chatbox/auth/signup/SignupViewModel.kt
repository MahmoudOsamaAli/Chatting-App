package com.example.chatbox.auth.signup

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.chatbox.repository.FireBaseAuthRepo
import com.example.chatbox.repository.FireBaseAuthRepo.getFireBaseAuth
import com.example.chatbox.utils.Constants
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

open class SignupViewModel(application: Application) : AndroidViewModel(application) {
    var phoneNumber: String? = null

    fun authorizePhoneNumber(context: Activity,
                             resendToken: PhoneAuthProvider.ForceResendingToken? = null,
                             callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {

            val options = PhoneAuthOptions.newBuilder(getFireBaseAuth())
            .setPhoneNumber(phoneNumber!!)
            .setTimeout(Constants.CALL_TIMEOUT, TimeUnit.SECONDS) // Timeout for the code sent via SMS
            .setActivity(context) // The activity to which the user is navigated to enter the code
            .setCallbacks(callbacks) // Implement PhoneAuthProvider.OnVerificationStateChangedCallbacks
        if (resendToken != null) {
            options.setForceResendingToken(resendToken)
        }
        FireBaseAuthRepo.authorizePhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, activity: Activity) =
        FireBaseAuthRepo.signInUserWithPhoneNumber(credential, activity)

}
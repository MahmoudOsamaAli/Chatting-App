package com.example.chatbox.auth.signup

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.chatbox.repository.FireBaseAuthRepo
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

open class SignupViewModel(application: Application) : AndroidViewModel(application) {
    var phoneNumber: String? = null

    fun authorizePhoneNumber(context: Activity,resendToken: PhoneAuthProvider.ForceResendingToken? = null,  callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) =
        FireBaseAuthRepo.authorizePhoneNumber(
            phoneNumber = phoneNumber!!,
            context = context,
            resendToken = resendToken,
            callbacks = callbacks
        )

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, activity: Activity) =
        FireBaseAuthRepo.signInUserWithPhoneNumber(credential, activity)

}
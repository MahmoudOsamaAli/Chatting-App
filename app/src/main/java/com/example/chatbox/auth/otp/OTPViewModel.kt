package com.example.chatbox.auth.otp

import android.app.Application
import com.example.chatbox.auth.signup.SignupViewModel
import com.example.chatbox.repository.FirebaseDatabaseRepo
import com.google.firebase.auth.PhoneAuthProvider

class OTPViewModel(application: Application) : SignupViewModel(application) {

    var OTP: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun checkIfUserExistInDataBase() = FirebaseDatabaseRepo.cheekIfPhoneExists(phoneNumber!!)
}
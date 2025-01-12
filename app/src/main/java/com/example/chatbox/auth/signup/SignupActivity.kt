package com.example.chatbox.auth.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.chatbox.BaseActivity
import com.example.chatbox.R
import com.example.chatbox.auth.otp.OTPActivity
import com.example.chatbox.databinding.ActivitySignupBinding
import com.example.chatbox.utils.Constants
import com.example.chatbox.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class SignupActivity : BaseActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val viewModel: SignupViewModel by viewModels()

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            signInUser(credential)
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
            hideDefaultLoading()
            showToast(getString(R.string.time_out))
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            hideDefaultLoading()
            showToast(p0.message.orEmpty())
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            // The SMS verification code has been sent to the provided phone number
            // Save the verification ID and the token to use later
            // Save verification ID and resending token so we can use them later
            hideDefaultLoading()
            val intent = Intent(this@SignupActivity, OTPActivity::class.java).apply {
                putExtra(Constants.RESEND_TOKEN, token)
                putExtra(Constants.OTP_KEY, verificationId)
                putExtra(Constants.PHONE_NUMBER_KEY, viewModel.phoneNumber)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrowIcon.setOnClickListener { onBackPressed() }

        binding.btnCreateAnAccount.setOnClickListener {
            viewModel.phoneNumber = "${Constants.EGYPT_PHONE_KEY}${binding.signupPhoneNumber.text}"

            if (viewModel.phoneNumber.isNullOrEmpty().not() && viewModel.phoneNumber?.length == 13) {
                showDefaultLoading()
                viewModel.authorizePhoneNumber(context = this, callbacks = callbacks)
            } else {
                showToast(getString(R.string.please_enter_a_valid_number))
            }

        }
    }
}
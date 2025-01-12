package com.example.chatbox.auth.otp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.chatbox.BaseActivity
import com.example.chatbox.R
import com.example.chatbox.auth.signup.UserInfoActivity
import com.example.chatbox.databinding.ActivityOtpBinding
import com.example.chatbox.main.MainActivity
import com.example.chatbox.network.ServerCallBack
import com.example.chatbox.utils.Constants
import com.example.chatbox.utils.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OTPActivity : BaseActivity() {

    private lateinit var binding : ActivityOtpBinding
    private val viewModel: OTPViewModel by viewModels()
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-retrieval or instant verification has succeeded
            // You can sign in the user here if needed
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            hideDefaultLoading()
            showToast(p0.message.orEmpty())
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
            hideDefaultLoading()
            showToast(getString(R.string.time_out))
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            viewModel.OTP = verificationId
            viewModel.resendToken = token
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        addTextChangeListener()

        binding.otpBtnVerify.setOnClickListener {
            val typedOTP = ( binding.otpEd1.text.toString() + binding.otpEd2.text.toString() +
                    binding.otpEd3.text.toString() + binding.otpEd4.text.toString() +
                    binding.otpEd5.text.toString() +  binding.otpEd6.text.toString() )
            if (typedOTP.isNotEmpty()){
                if (typedOTP.length == 6){
                    signInWithPhoneAuthCredential(
                        PhoneAuthProvider.getCredential(
                            viewModel.OTP.orEmpty(), typedOTP
                        )
                    )

                } else {
                    showToast(getString(R.string.invalid_otp))
                }
            } else {
                showToast(getString(R.string.invalid_otp))
            }
        }

        binding.resendTextView.setOnClickListener {
            viewModel.authorizePhoneNumber(this, viewModel.resendToken, callbacks)
        }

    }

    private fun getData() {
        viewModel.OTP = intent.getStringExtra(Constants.OTP_KEY).toString()
        viewModel.resendToken = intent.getParcelableExtra(Constants.RESEND_TOKEN)!!
        viewModel.phoneNumber = intent.getStringExtra(Constants.PHONE_NUMBER_KEY)!!
    }

    private fun addTextChangeListener() {
            binding.otpEd1.addTextChangedListener(EditTextWatcher(binding.otpEd1))
            binding.otpEd2.addTextChangedListener(EditTextWatcher(binding.otpEd2))
            binding.otpEd3.addTextChangedListener(EditTextWatcher(binding.otpEd3))
            binding.otpEd4.addTextChangedListener(EditTextWatcher(binding.otpEd4))
            binding.otpEd5.addTextChangedListener(EditTextWatcher(binding.otpEd5))
            binding.otpEd6.addTextChangedListener(EditTextWatcher(binding.otpEd6))}
    inner class EditTextWatcher(private val view:View):TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //blank
            }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //blank
            }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when(view.id){
                R.id.otp_ed_1 -> if (text.length == 1) binding.otpEd2.requestFocus()
                R.id.otp_ed_2 -> if (text.length == 1) binding.otpEd3.requestFocus()else if (text.isEmpty()) binding.otpEd1
                R.id.otp_ed_3 -> if (text.length == 1) binding.otpEd4.requestFocus()else if (text.isEmpty()) binding.otpEd2
                R.id.otp_ed_4 -> if (text.length == 1) binding.otpEd5.requestFocus()else if (text.isEmpty()) binding.otpEd3
                R.id.otp_ed_5 -> if (text.length == 1) binding.otpEd6.requestFocus()else if (text.isEmpty()) binding.otpEd4
                R.id.otp_ed_6 -> if (text.isEmpty()) binding.otpEd5.requestFocus()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        viewModel.signInWithPhoneAuthCredential(credential, this).observe(this) {
            when (it.status) {
                ServerCallBack.Status.LOADING -> {
                    showDefaultLoading()
                }

                ServerCallBack.Status.SUCCESS -> {
                    hideDefaultLoading()
                    showToast(it.data?.message.orEmpty())
                    cheekIfPhoneExists()
                }

                ServerCallBack.Status.ERROR -> {
                    hideDefaultLoading()
                    showToast(it.data?.message.orEmpty())
                }
            }
        }
    }

    private fun cheekIfPhoneExists() {
        viewModel.checkIfUserExistInDataBase().observe(this) {
            when (it.status) {
                ServerCallBack.Status.LOADING -> {
                    showDefaultLoading()
                }

                ServerCallBack.Status.SUCCESS -> {
                    hideDefaultLoading()
//                    if (it.data?.isUserExists == true) {
//                        startActivity(Intent(this, MainActivity::class.java))
//                    } else {
                        startActivity(Intent(this, UserInfoActivity::class.java))
//                    }
                    finish()
                }

                ServerCallBack.Status.ERROR -> {
                    hideDefaultLoading()
                    showToast(it.data?.message.orEmpty())
                }
            }
        }
    }

}
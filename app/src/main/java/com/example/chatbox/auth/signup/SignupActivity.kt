package com.example.chatbox.auth.signup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbox.auth.startupActivity.StartupActivity
import com.example.chatbox.databinding.ActivitySignupBinding
import com.example.chatbox.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class
SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var number : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.backArrowIcon.setOnClickListener { backToStartupActivity() }
        binding.btnCreateAnAccount.setOnClickListener {
            number = binding.signupPhoneNumber.text.toString()
            if(number.isNotEmpty()) {
                if (number.length == 10)
                {
                    number = "+20$number"
                    authFirebase()

                }else{Toast.makeText(this,"Please Enter correct Number",Toast.LENGTH_LONG).show()}
            }else{Toast.makeText(this,"Please Enter Number",Toast.LENGTH_LONG).show()}

        }
    }
    private fun authFirebase() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout for the code sent via SMS
            .setActivity(this) // The activity to which the user is navigated to enter the code
            .setCallbacks(callbacks) // Implement PhoneAuthProvider.OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@SignupActivity,"Authenticate Successfully",Toast.LENGTH_LONG).show()
                    swapToMainActivity()
                } else {
                    Toast.makeText(this@SignupActivity,"Authenticate failed ",Toast.LENGTH_LONG).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    //blank
                    }
                    // Update UI
                }
            }
    }
    private fun swapToMainActivity() {

        startActivity(Intent(this, MainActivity::class.java))
    }
    val callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(p0: FirebaseException) {
//55
        }
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {

            // The SMS verification code has been sent to the provided phone number
            // Save the verification ID and the token to use later
            // Save verification ID and resending token so we can use them later
            var resendToken = token
            val intent = Intent(this@SignupActivity,OTPActivity::class.java)
            intent.apply {
                putExtra("resendToken",resendToken)
                putExtra("OTP",verificationId)
                putExtra("phoneNumber",number)
            }
            startActivity(intent)
            finish()


        }


    }



    private fun backToStartupActivity() {
        val intent = Intent(this, StartupActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun setStatusBar() {
        // to make the status bar transparent
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.TRANSPARENT
        // to change status bar title color
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }



}
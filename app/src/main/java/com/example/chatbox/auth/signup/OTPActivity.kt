package com.example.chatbox.auth.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbox.R
import com.example.chatbox.databinding.ActivityOtpBinding
import com.example.chatbox.main.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var binding : ActivityOtpBinding

    private lateinit var OTP : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber : String

    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var userRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        getData()
        addTextChangeListener()
        binding.otpBtnVerify.setOnClickListener {
            val typedOTP = ( binding.otpEd1.text.toString() + binding.otpEd2.text.toString() +
                    binding.otpEd3.text.toString() + binding.otpEd4.text.toString() +
                    binding.otpEd5.text.toString() +  binding.otpEd6.text.toString() )
            if (typedOTP.isNotEmpty()){
                if (typedOTP.length == 6){
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        OTP,typedOTP
                    )
                    signInWithPhoneAuthCredential(credential)

                }else{
                    Toast.makeText(this,"Please Enter correct OTP",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Please Enter OTP",Toast.LENGTH_LONG).show()
            }
        }

        binding.resendTextView.setOnClickListener {
            resendVerificationCode()
        }

    }
    private fun resendVerificationCode(){
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout for the code sent via SMS
            .setActivity(this) // The activity to which the user is navigated to enter the code
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Auto-retrieval or instant verification has succeeded
            // You can sign in the user here if needed
        }    override fun onVerificationFailed(e: FirebaseException) {
            // Verification failed
        }
        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            OTP = verificationId
            resendToken = token
        }
    }
    private fun getData() {
        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNumber = intent.getStringExtra("phoneNumber")!!
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
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@OTPActivity,"Authenticate Successfully",Toast.LENGTH_LONG).show()
                    cheekIfPhoneExists()
                } else {
                    Toast.makeText(this@OTPActivity,"Authenticate failed",Toast.LENGTH_LONG).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    }
                    // Update UI
                }
            }
    }
    private fun swapToHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun swapToUserInfoActivity() {
        val intent = Intent(this, UserInfoActivity::class.java)
        intent.putExtra("phoneNum",phoneNumber)
        startActivity(intent)
        finish()
    }
    private fun cheekIfPhoneExists(){
        userRef = database.getReference("users")
        userRef.orderByChild("phoneNumber").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        swapToHomeActivity()
                    }else
                    {
                        swapToUserInfoActivity()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}", error.toException())
                    Toast.makeText(this@OTPActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }

            })
    }

}
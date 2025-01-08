package com.example.chatbox.auth.signup

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatbox.data.User
import com.example.chatbox.databinding.ActivityUserInfoBinding
import com.example.chatbox.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserInfoActivity : AppCompatActivity() {
    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var fullName: String
    lateinit var bio: String
    lateinit var phoneNumber: String
    lateinit var pictureUri : Uri
    lateinit var userUid : String

    lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        phoneNumber = intent.getStringExtra("phoneNum")!!

        binding.userinfoAddNewProfilePicture.setOnClickListener {
            openTheGallery()
        }

        binding.userinfoBtnJoin.setOnClickListener {
            firstName = binding.userinfoEdFirstName.text.toString()
            lastName = binding.userinfoEdLastName.text.toString()
            fullName = "$firstName $lastName"
            bio = binding.userinfoEdFirstName.text.toString()

            swapToMainActivity()
        }
    }

    private fun openTheGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun swapToMainActivity() {
        addUserToDB(fullName, bio, phoneNumber, true, auth.currentUser?.uid!!)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addUserToDB(
        name: String,
        bio: String,
        phoneNumber: String,
        userState: Boolean,
        uid: String
    ) {
        userUid = uid
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        val user = User(
            userId = uid,
            userName = name,
            profilePicture = pictureUri.toString(),
            phoneNumber = phoneNumber,
            isUserOnline = userState

        )

        usersRef.child(uid).setValue(user)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let {
                binding.userinfoImgProfilePicture.setImageURI(it)
                pictureUri = it
                updateUserProfilePicture(it.toString())
            }
        }
    }

    private fun updateUserProfilePicture(imageUri: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userUid)
        userRef.child("profilePicture").setValue(imageUri)
            .addOnSuccessListener {
                Toast.makeText(this,"Profile picture updated successfully",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{ exception ->
                Log.e("Firebase", "Failed to update profile picture", exception)
            }

    }


}
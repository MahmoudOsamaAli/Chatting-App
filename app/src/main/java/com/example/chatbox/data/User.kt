package com.example.chatbox.data

import android.net.Uri

data class User(
    val userId : String = "",
    val userName : String = "",
    val profilePicture : String = "" ,
    val phoneNumber : String = "",
    val isUserOnline : Boolean = false
)

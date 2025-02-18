package com.example.chatbox.data


data class User(
    var userId: String = "",
    var userName: String = "",
    var profilePicture: String = "",
    var phoneNumber: String = "",
    var userState: String = "",
    val bio: String =""
)

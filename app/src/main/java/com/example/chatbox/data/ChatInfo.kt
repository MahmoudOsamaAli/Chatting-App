package com.example.chatbox.data

data class ChatInfo(
    val name: String = "",
    val phoneNumber: String = "",
    val profilePic: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val lastMessageState: Boolean = false,
    val lastMessageSenderId: String = "",
    val isSilent: Boolean = false
)

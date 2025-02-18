package com.example.chatbox.data

data class Message(
    val messageId: String = "",
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    var isSeen: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

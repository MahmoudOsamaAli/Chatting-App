package com.example.chatbox.data

import android.os.Message

data class Chat(
    val user : List<String> = listOf(),
    val message : Map<String , Message> = mapOf()
)

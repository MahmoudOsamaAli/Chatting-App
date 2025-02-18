package com.example.chatbox.data

data class Chat(
    val user: List<String> = listOf(),
    val message: Map<String, com.example.chatbox.data.Message> = mapOf()
)

package com.example.chatbox.data.response

import com.example.chatbox.data.BaseResponse

data class DatabaseResponse(override val success: Boolean, override val message: String, val isUserExists: Boolean):BaseResponse

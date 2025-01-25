package com.example.chatbox.data.response

import com.example.chatbox.data.BaseResponse

data class AuthResponse(override val success: Boolean, override val message: String) : BaseResponse
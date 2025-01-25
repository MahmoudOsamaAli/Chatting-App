package com.example.chatbox.data.response

import com.example.chatbox.data.BaseResponse

data class ImgurResponse(
    val data: ImgurData,
    override val success: Boolean,
    override val message: String,
    val status: Int,
) : BaseResponse

data class ImgurData(
    val link: String
)
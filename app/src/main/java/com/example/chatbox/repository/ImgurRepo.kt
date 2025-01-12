package com.example.chatbox.repository

import com.example.chatbox.network.api.ImgurApi
import com.example.chatbox.network.networkCall.NetworkCall
import com.example.chatbox.network.networkCall.ServiceFactory
import okhttp3.MultipartBody

object ImgurRepo {

    fun uploadImage(image: MultipartBody.Part) = NetworkCall.makeCall {
        ServiceFactory.create(ImgurApi::class.java).uploadImage(image)
    }
}
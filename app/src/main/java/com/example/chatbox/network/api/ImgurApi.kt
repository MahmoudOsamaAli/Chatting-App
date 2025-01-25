package com.example.chatbox.network.api

import com.example.chatbox.data.response.ImgurResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgurApi {
    @Multipart
    @POST("3/image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ImgurResponse>
}
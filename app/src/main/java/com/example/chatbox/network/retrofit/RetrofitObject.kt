package com.example.chatbox.network.retrofit

import com.example.chatbox.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitObject private constructor() {
    companion object {
        private const val BASE_URL = "https://api.imgur.com/"

        private var ourInstance: Retrofit? = null

        private val clientInterceptor = OkHttpClient().newBuilder()
            .addInterceptor(ClientInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .callTimeout(4, TimeUnit.MINUTES)
            .writeTimeout(4, TimeUnit.MINUTES)
            .readTimeout(4, TimeUnit.MINUTES)
            .build()

        @Synchronized
        fun getInstance(): Retrofit? {

            if (ourInstance == null) {
                ourInstance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientInterceptor)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return ourInstance
        }
    }
}

class ClientInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val request = request().newBuilder()
            .addHeader("Authorization", "Client-ID ${Constants.IMAGUR_CLIENT_ID}")
        proceed(request.build())
    }
}
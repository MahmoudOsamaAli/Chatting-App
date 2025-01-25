package com.wefaq.carsapp.network.retrofit

import com.example.chatbox.network.retrofit.RetrofitObject

class ServiceFactory {

    companion object {

        @Synchronized
        fun <T> create(service: Class<T>): T {
            return RetrofitObject.getInstance()?.create(service)!!
        }
    }
}
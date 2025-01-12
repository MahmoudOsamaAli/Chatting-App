package com.example.chatbox.network.networkCall

import androidx.lifecycle.MutableLiveData
import com.example.chatbox.data.BaseResponse
import com.example.chatbox.network.ServerCallBack
import com.example.chatbox.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.reflect.Type
import java.net.ConnectException

object NetworkCall {

    fun <T : BaseResponse> makeCall(requestFun: suspend () -> Response<T>): MutableLiveData<ServerCallBack<T>> {
        val result = MutableLiveData<ServerCallBack<T>>()
        //this is for showing loading on the screen
        result.value = ServerCallBack.loading()
        //this is for making the call inside CoroutineScope
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = requestFun()
                withContext(Dispatchers.Main) {
                    when {
                        response.isSuccessful -> {
                            response.body()?.let { body ->
                                result.value = ServerCallBack.success(null, body)
                                return@withContext
                            }
                        }

                        else -> result.value = ServerCallBack.error(response.errorBody()?.string().orEmpty())
                    }
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    if (e is ConnectException) {
                        //this is no internet exception
                        result.value = ServerCallBack.error(Constants.NO_INTERNET)
                    } else
                        result.value = ServerCallBack.error(Constants.GENERAL_ERROR)
                }
            }
        }
        return result
    }
}
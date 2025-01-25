package com.example.chatbox.repository

import androidx.lifecycle.MutableLiveData
import com.example.chatbox.data.User
import com.example.chatbox.data.response.DatabaseResponse
import com.example.chatbox.network.ServerCallBack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseRepo {

    private const val USERS_REF = "users"
    private const val PHONE_NUMBER_REF = "phoneNumber"

    private fun getDatabase() = FirebaseDatabase.getInstance()

    fun cheekIfPhoneExists(phoneNumber: String): MutableLiveData<ServerCallBack<DatabaseResponse>> {
        val liveData = MutableLiveData<ServerCallBack<DatabaseResponse>>()
        liveData.value = ServerCallBack.loading()
        val userRef = getDatabase().getReference(USERS_REF)
        userRef.orderByChild(PHONE_NUMBER_REF).equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        liveData.value = ServerCallBack.success(data = DatabaseResponse(success = true, message = "", isUserExists = true))
                    } else {
                        liveData.value = ServerCallBack.success(data = DatabaseResponse(success = true, message = "", isUserExists = false))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    liveData.value = ServerCallBack.error(message = error.message, data = DatabaseResponse(success = false, message = error.message, isUserExists = false))
                }

            })
        return liveData
    }

    fun saveUserToDB(user: User): MutableLiveData<ServerCallBack<DatabaseResponse>> {
        val liveData = MutableLiveData<ServerCallBack<DatabaseResponse>>()
        liveData.value = ServerCallBack.loading()
        val userRef = getDatabase().getReference(USERS_REF)
        userRef.child(user.userId!!).setValue(user).addOnSuccessListener {
            liveData.value = ServerCallBack.success(data = DatabaseResponse(success = true, message = "", isUserExists = true))
        }.addOnFailureListener { t ->
            liveData.value = ServerCallBack.error(message = t.message.orEmpty(), data = DatabaseResponse(success = false, message = t.message.orEmpty(), isUserExists = false))
        }
        return liveData
    }
}
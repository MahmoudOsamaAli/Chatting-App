package com.example.chatbox.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chatbox.data.Chat
import com.example.chatbox.data.ChatInfo
import com.example.chatbox.data.Message
import com.example.chatbox.data.User
import com.example.chatbox.data.response.DatabaseResponse
import com.example.chatbox.network.ServerCallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

object FirebaseDatabaseRepo {

    private const val PHONE_NUMBER_REF = "phoneNumber"
    private const val USERS_REF = "users"
    private const val CHATS_REF = "chats"

    private val usersRef: DatabaseReference by lazy { getDatabase().reference.child(USERS_REF) }
    private val chatsRef: DatabaseReference by lazy { getDatabase().reference.child(CHATS_REF) }

    private val currentUserId = FireBaseAuthRepo.getUserUUID()
    private var currentUserProfilePic: String = ""


    private fun getDatabase() = FirebaseDatabase.getInstance()

    fun cheekIfPhoneExists(phoneNumber: String): MutableLiveData<ServerCallBack<DatabaseResponse>> {
        val liveData = MutableLiveData<ServerCallBack<DatabaseResponse>>()
        liveData.value = ServerCallBack.loading()
        val userRef = getDatabase().getReference(USERS_REF)
        userRef.orderByChild(PHONE_NUMBER_REF).equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        liveData.value = ServerCallBack.success(
                            data = DatabaseResponse(
                                success = true,
                                message = "",
                                isUserExists = true
                            )
                        )
                    } else {
                        liveData.value = ServerCallBack.success(
                            data = DatabaseResponse(
                                success = true,
                                message = "",
                                isUserExists = false
                            )
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    liveData.value = ServerCallBack.error(
                        message = error.message,
                        data = DatabaseResponse(
                            success = false,
                            message = error.message,
                            isUserExists = false
                        )
                    )
                }

            })
        return liveData
    }

    fun saveUserToDB(user: User): MutableLiveData<ServerCallBack<DatabaseResponse>> {
        val liveData = MutableLiveData<ServerCallBack<DatabaseResponse>>()
        liveData.value = ServerCallBack.loading()
        val userRef = getDatabase().getReference(USERS_REF)
        userRef.child(user.userId).setValue(user).addOnSuccessListener {
            liveData.value = ServerCallBack.success(
                data = DatabaseResponse(
                    success = true,
                    message = "",
                    isUserExists = true
                )
            )
        }.addOnFailureListener { t ->
            liveData.value = ServerCallBack.error(
                message = t.message.orEmpty(),
                data = DatabaseResponse(
                    success = false,
                    message = t.message.orEmpty(),
                    isUserExists = false
                )
            )
        }
        return liveData
    }

    fun getUserInfo(phoneNumber: String): LiveData<User?> {
        val userInfoLiveData = MutableLiveData<User?>()

        usersRef.orderByChild(PHONE_NUMBER_REF).equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userSnapshot = snapshot.children.firstOrNull()

                    if (userSnapshot != null) {
                        val user = userSnapshot.getValue(User::class.java)
                        userInfoLiveData.postValue(user)
                    } else {
                        userInfoLiveData.postValue(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    userInfoLiveData.postValue(null)
                }
            })

        return userInfoLiveData
    }

//    fun saveChatUserInfo(chatId: String, userId: String, name: String) {
//        val userInfoRef = chatsRef.child(chatId).child("chatInfo").child(userId)
//
//        val currentUserInfo = hashMapOf(
//            "name" to name,
//            "profilePic" to currentUserProfilePic,
//        )
//        userInfoRef.updateChildren(currentUserInfo as Map<String, Any>)
//            .addOnCompleteListener { task ->
//
//            }
//
//    }

    fun checkIfChatExists(chatId: String): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        val chatRef = chatsRef.child(chatId)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                liveData.postValue(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(false)
            }
        })

        return liveData
    }

    fun listenForMessages(chatId: String): LiveData<Chat> {
        val liveData = MutableLiveData<Chat>()
        val chatMessagesRef = chatsRef.child(chatId)

        chatMessagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val messages = snapshot.child("messages")
                        .children
                        .mapNotNull { it.getValue(Message::class.java) }
                        .associateBy { it.messageId }

                    val users = snapshot.child("users").children.mapNotNull { it.value.toString() }
                    val chat = Chat(users, messages)
                    liveData.postValue(chat)
                } else {
                    liveData.postValue(Chat())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(Chat())
            }
        })

        return liveData
    }

    fun sendMessage(
        chatId: String,
        message: Message,
        userId1: String,
        userId2: String
    ): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        val chatRef = chatsRef.child(chatId)

        val chatData = hashMapOf(
            "users" to hashMapOf(
                userId1 to true,
                userId2 to true
            )
        )
        chatRef.updateChildren(chatData as Map<String, Any>).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val chatMessagesRef = chatRef.child("messages")
                val newMessageRef = chatMessagesRef.push()
                val messageData = message.copy(isSeen = false, messageId = newMessageRef.key ?: "")
                newMessageRef.setValue(messageData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val summaryData = hashMapOf<String, Any>(
                                "lastMessage" to message.message,
                                "lastMessageTime" to message.timestamp,
                                "lastMessageState" to message.isSeen,
                                "lastMessageSenderId" to message.senderId
                            )
                            chatRef.child("chatInfo").updateChildren(summaryData)
                                .addOnCompleteListener { summaryTask ->
                                    liveData.postValue(summaryTask.isSuccessful)
                                }.addOnFailureListener { liveData.postValue(false) }
                        } else {
                            liveData.postValue(false)
                        }
                    }
                    .addOnFailureListener { liveData.postValue(false) }
            } else {
                liveData.postValue(false)
            }
        }.addOnFailureListener {
            liveData.postValue(false)
        }
        return liveData
    }


    fun markMessageAsSeen(chatId: String, messageId: String, userId: String): LiveData<Boolean> {
        val resultLiveData = MutableLiveData<Boolean>()
        val messageRef = FirebaseDatabase.getInstance()
            .getReference(CHATS_REF)
            .child(chatId)
            .child("messages")
            .child(messageId)
        messageRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val message =
                    mutableData.getValue(Message::class.java) ?: return Transaction.success(
                        mutableData
                    )
                if (message.senderId != userId && !message.isSeen) {
                    message.isSeen = true
                    mutableData.value = message
                    val chatInfoStateRef = FirebaseDatabase.getInstance()
                        .getReference(CHATS_REF)
                        .child(chatId)
                        .child("chatInfo")

                    val update = hashMapOf<String, Any>(
                        "lastMessageState" to true
                    )
                    chatInfoStateRef.updateChildren(update)


                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {
                if (error != null) {
                    resultLiveData.postValue(false)
                } else {
                    resultLiveData.postValue(true)
                }
            }
        })


        return resultLiveData
    }


    fun checkUserState(userId: String): MutableLiveData<String> {
        val liveData = MutableLiveData<String>()
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.getValue(String::class.java) ?: ""
                liveData.postValue(status)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to check user status", error.toException())
            }
        }

        val userStatusRef = usersRef.child(userId).child("userState")
        userStatusRef.addValueEventListener(listener)


        return liveData
    }

    fun getProfilePicture(userId: String): LiveData<String> {
        val liveData = MutableLiveData<String>()
        usersRef
            .child(userId)
            .child("profilePicture")
            .get()
            .addOnSuccessListener { snapshot ->
                currentUserProfilePic = snapshot.value.toString()
                liveData.postValue(currentUserProfilePic)
            }
            .addOnFailureListener { liveData.postValue("") }
        return liveData
    }


    fun getChats(): LiveData<List<ChatInfo>> {
        val liveData = MutableLiveData<List<ChatInfo>>()
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatListTemp = mutableListOf<ChatInfo>()
                val tasks = mutableListOf<Task<DataSnapshot>>()

                for (chatSnapshot in snapshot.children) {
                    val usersSnapshot = chatSnapshot.child("users")
                    if (usersSnapshot.hasChild(currentUserId!!)) {
                        val chatInfoSnapshot = chatSnapshot.child("chatInfo")
                        val lastMessage = chatInfoSnapshot.child("lastMessage").value.toString()
                        val lastMessageTime =
                            chatInfoSnapshot.child("lastMessageTime").getValue(Long::class.java)
                                ?: 0L
                        val lastMessageState =
                            chatInfoSnapshot.child("lastMessageState").value as? Boolean ?: false
                        val lastMessageSender =
                            chatInfoSnapshot.child("lastMessageSenderId").value.toString()
                        var contactUserId: String? = null
                        for (user in usersSnapshot.children) {
                            if (user.key != currentUserId) {
                                contactUserId = user.key
                                break
                            }
                        }

                        if (contactUserId != null) {
                            val userTask = usersRef.child(contactUserId).get()
                            tasks.add(userTask)

                            userTask.addOnSuccessListener { userSnapshot ->
                                val contactName = userSnapshot.child("userName").value.toString()
                                val contactPic =
                                    userSnapshot.child("profilePicture").value.toString()
                                val contactNumber =
                                    userSnapshot.child("phoneNumber").value.toString()

                                chatListTemp.add(
                                    ChatInfo(
                                        contactName,
                                        contactNumber,
                                        contactPic,
                                        lastMessage,
                                        lastMessageTime,
                                        lastMessageState,
                                        lastMessageSender
                                    )
                                )
                                if (tasks.all { it.isComplete }) {
                                    liveData.postValue(chatListTemp)
                                }
                            }.addOnFailureListener {
                                Log.e("Firebase", "Error fetching user data", it)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching chats", error.toException())
            }
        })

        return liveData
    }
}



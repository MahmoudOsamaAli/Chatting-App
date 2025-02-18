package com.example.chatbox.main.fragments.home.Chats.ChatMessages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatbox.data.Chat
import com.example.chatbox.data.Message
import com.example.chatbox.data.User
import com.example.chatbox.repository.FireBaseAuthRepo
import com.example.chatbox.repository.FirebaseDatabaseRepo

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<Chat>()
    val messages: LiveData<Chat> get() = _messages

    private val _sendMessageStatus = MutableLiveData<Boolean>()
    val sendMessageStatus: LiveData<Boolean> get() = _sendMessageStatus

    private val _user = MutableLiveData<List<User>>()
    val user: LiveData<List<User>> get() = _user

    private val _seenMark = MutableLiveData<Boolean>()
    val seenMark: LiveData<Boolean> get() = _seenMark

    val getUserId: String
        get() = FireBaseAuthRepo.getUserUUID() ?: ""

    fun getUserInfo(phoneNumber: String): LiveData<User?> {
        return FirebaseDatabaseRepo.getUserInfo(phoneNumber)
    }

    fun generateChatId(currentUserId: String, contactUserId: String): String {
        val sorted = listOf(currentUserId, contactUserId).sorted()
        return "${sorted[0]}_${sorted[1]}"
    }

    fun checkIfChatExists(chatId: String): LiveData<Boolean> {
        return FirebaseDatabaseRepo.checkIfChatExists(chatId)
    }

    fun listenForMessages(chatId: String) {
        val liveData = FirebaseDatabaseRepo.listenForMessages(chatId)
        liveData.observeForever { chat ->
            _messages.postValue(chat)
        }
    }

    fun sendMessage(chatId: String, message: Message, currentUserId: String, contactUserId: String) {
        FirebaseDatabaseRepo.sendMessage(chatId, message, currentUserId, contactUserId).observeForever { status ->
            _sendMessageStatus.postValue(status)
        }
    }

    fun markMessageAsSeen(chatId: String, messageId: String, userId: String): LiveData<Boolean> {
        return FirebaseDatabaseRepo.markMessageAsSeen(chatId, messageId, userId)
    }

    fun checkUserState(userId: String): LiveData<String> {
        return FirebaseDatabaseRepo.checkUserState(userId)
    }

//    fun saveUserInfo(chatId: String, userId: String, name: String) {
//        FirebaseDatabaseRepo.saveChatUserInfo(chatId, userId, name)
//    }

}

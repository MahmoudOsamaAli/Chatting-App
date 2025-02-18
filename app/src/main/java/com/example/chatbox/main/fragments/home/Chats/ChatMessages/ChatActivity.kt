package com.example.chatbox.main.fragments.home.Chats.ChatMessages

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatbox.R
import com.example.chatbox.data.Message
import com.example.chatbox.data.User
import com.example.chatbox.databinding.ActivityChatBinding
import com.example.chatbox.utils.showToast

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var messagesAdapter: ChatMessagesAdapter
    private var chatId: String? = null
    private var currentUserId: String? = null
    private var contactId: String? = null
    private var contactName: String? = null
    private var contactState: String? = null
    private var contactPhone: String? = null
    private var contactProfilePic: String? = null
    private var contactBio: String? = null
    private var homeOrContacts: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        val homePhoneNumber = intent.getStringExtra("HomePhoneNumber") ?: ""
        val contactsPhoneNumber = intent.getStringExtra("PhoneNumberContacts") ?: ""
        homeOrContacts = contactsPhoneNumber.ifEmpty { homePhoneNumber }

        chatViewModel.getUserInfo(homeOrContacts!!).observe(this) { user ->
            if (user != null) {
                getChatData(user)
                contactName = user.userName
                contactPhone = user.phoneNumber
                contactState = user.userState
                contactProfilePic = user.profilePicture
                contactBio = user.bio
                binding.chatUserName.text = contactName
                loadProfilePicture(user.profilePicture)
                setupRecyclerView()
                setupChat()
                setupObservers()
            } else {
                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show()
            }
        }

        setContentView(binding.root)

        binding.chatIcSend.setOnClickListener {
            manageTheMessage()
        }
        binding.chatIcBackArrow.setOnClickListener {
            onBackPressed()
        }
        binding.chatUserInfo.setOnClickListener {
            val userInfoFragment = UserInfoFragment.newInstance(
                contactName!!, contactProfilePic!!, contactPhone!!, contactState!!,
                contactBio!!
            )
            supportFragmentManager.beginTransaction()
                .add(android.R.id.content, userInfoFragment)
                .addToBackStack(null)
                .commit()
        }
        switchToSendIcon()
    }

    private fun getChatData(user: User) {
        contactId = user.userId
        currentUserId = chatViewModel.getUserId
        chatId = chatViewModel.generateChatId(currentUserId!!, contactId!!)
    }

    private fun loadProfilePicture(profilePicture: String) {
        Glide.with(this)
            .load(profilePicture)
            .placeholder(R.drawable.img_profile_empty)
            .into(binding.chatUserProfileImage)
    }

    private fun setupChat() {
        chatId?.let {
            chatViewModel.checkIfChatExists(it).observe(this) { chatExists ->
                if (chatExists) {
                    chatViewModel.listenForMessages(it)
                }
            }
        } ?: run {
            showToast("Invalid chat ID")
        }
    }

    private fun setupRecyclerView() {
        messagesAdapter = ChatMessagesAdapter(currentUserId ?: "")
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        binding.chatRecyclerView.apply {
            layoutManager = manager
            adapter = messagesAdapter
        }
    }

    private fun setupObservers() {
        chatViewModel.messages.observe(this) { chat ->
            val messageList = chat.message.map { it.value }
            messagesAdapter.submitList(messageList)
            chat.message.forEach { message ->
                binding.chatRecyclerView.smoothScrollToPosition(messageList.size - 1)
                if (!message.value.isSeen && message.value.receiverId == currentUserId) {
                    chatViewModel.markMessageAsSeen(
                        chatId!!,
                        message.value.messageId,
                        currentUserId!!
                    )
                        .observe(this) { success ->

                        }
                }
            }
        }
        contactId?.let {
            chatViewModel.checkUserState(it).observe(this) { userState ->
                binding.chatUserState.text = userState
                if (userState == "Online") {
                    binding.chatIvUserOnlineStatus.visibility = VISIBLE
                } else {
                    binding.chatIvUserOnlineStatus.visibility = INVISIBLE
                }
            }
        }
    }

    private fun manageTheMessage() {
        val messageText = binding.chatEtNewMessage.text.toString().trim()
        if (messageText.isNotEmpty() && chatId != null) {
            val message = Message(
                senderId = currentUserId!!,
                receiverId = contactId!!,
                message = messageText,
                timestamp = System.currentTimeMillis()
            )

            chatViewModel.sendMessage(chatId!!, message, currentUserId!!, contactId!!)
            binding.chatEtNewMessage.text.clear()

        } else {
            Toast.makeText(this, "Unable to send message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchToSendIcon() {
        binding.chatEtNewMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(e: Editable?) {
                if (e.isNullOrEmpty()) {
                    binding.chatIcSend.visibility = GONE
                    binding.chatIcMicRecord.visibility = VISIBLE
                    binding.chatIcGallery.visibility = VISIBLE
                } else {
                    binding.chatIcMicRecord.visibility = GONE
                    binding.chatIcGallery.visibility = GONE
                    binding.chatIcSend.visibility = VISIBLE
                }
            }
        })
    }

}

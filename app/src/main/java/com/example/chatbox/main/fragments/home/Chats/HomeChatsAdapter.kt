package com.example.chatbox.main.fragments.home.Chats

import MessagesDiffCallback
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatbox.R
import com.example.chatbox.data.ChatInfo
import com.example.chatbox.databinding.ItemChatBinding
import com.example.chatbox.main.fragments.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeChatsAdapter(
    private var chatsList: List<ChatInfo>,
    private val currentUserId: String,
    private val listener: HomeFragment

) : RecyclerView.Adapter<HomeChatsAdapter.ChatsViewHolder>() {
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    inner class ChatsViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatInfo: ChatInfo) {
            val phoneNumber = chatInfo.phoneNumber

            binding.apply {
                userName.text = chatInfo.name
                messageTime.text = timeFormat.format(Date(chatInfo.lastMessageTime))
                lastMessage.text = chatInfo.lastMessage
            }

            binding.icNewMessage.setImageResource(
                if (chatInfo.lastMessageSenderId != currentUserId){
                    if (!chatInfo.lastMessageState)R.drawable.ic_home_new_messages else 0
                }else{0}
            )

            binding.icMessageState.setImageResource(
                if (chatInfo.lastMessageSenderId == currentUserId) {
                    if (chatInfo.lastMessageState) R.drawable.ic_seen_black else R.drawable.ic_home_unseen
                } else {
                    0
                }
            )

            Glide.with(binding.root.context)
                .load(chatInfo.profilePic)
                .placeholder(R.drawable.img_profile_empty)
                .error(R.drawable.img_profile_empty)
                .centerCrop()
                .into(binding.contactImageProfile)

            binding.root.setOnClickListener {
                listener.openChatActivity(chatInfo.name, phoneNumber, chatInfo.profilePic)
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(chatsList[position])
    }

    override fun getItemCount(): Int = chatsList.size

    fun updateChats(newMessagesList: List<ChatInfo>) {
        val diffCallback = MessagesDiffCallback(chatsList, newMessagesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        chatsList = newMessagesList
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(position: Int): ChatInfo? {
        if (position < 0 || position >= chatsList.size) return null
        val newList = chatsList.toMutableList()
        val removedItem = newList.removeAt(position)
        updateChats(newList)
        return removedItem
    }

    fun restoreItem(chatInfo: ChatInfo, position: Int) {
        val newList = chatsList.toMutableList()
        newList.add(position, chatInfo)
        updateChats(newList)
    }

    fun toggleSilentItem(position: Int) {
        if (position in chatsList.indices) {
            val newList = chatsList.toMutableList()
            newList[position] = newList[position].copy(isSilent = !newList[position].isSilent)
            chatsList = newList
            notifyItemChanged(position)
        }
    }

    fun getSilentState(position: Int): Boolean = chatsList.getOrNull(position)?.isSilent ?: false
}

package com.example.chatbox.main.fragments.home.Chats

import MessagesDiffCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.data.FakeData
import com.example.chatbox.databinding.ItemChatBinding
import com.example.chatbox.main.fragments.home.HomeFragment

class HomeChatsAdapter(
    private var chatsList: List<FakeData.ChatInfo>,
    private val listener: HomeFragment
) : RecyclerView.Adapter<HomeChatsAdapter.ChatsViewHolder>() {
    inner class ChatsViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatInfo: FakeData.ChatInfo) {
            binding.userName.text = chatInfo.name
            binding.lastMessage.text = chatInfo.content
            // Handle the silent status indicator
            binding.silentIcon.visibility =
                if (chatInfo.isSilent) View.VISIBLE else View.GONE

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val binding = ItemChatBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatsViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(chatsList[position])
        holder.itemView.setOnClickListener {
            listener.onItemClick(chatsList[position])
        }
    }
    override fun getItemCount(): Int {
        return chatsList.size
    }
    /**
     * Updates the list using DiffUtil for efficient changes.
     */
    private fun updateChats(newMessagesList: List<FakeData.ChatInfo>) {
        val diffCallback = MessagesDiffCallback(chatsList, newMessagesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        chatsList = newMessagesList
        diffResult.dispatchUpdatesTo(this)
    }
    fun removeItem(position: Int): FakeData.ChatInfo? {
        if (position < 0 || position >= chatsList.size) return null
        val newList = chatsList.toMutableList()
        val removedItem = newList.removeAt(position)
        updateChats(newList)
        return removedItem
    }
    fun restoreItem(chatInfo: FakeData.ChatInfo, position: Int) {
        val newList = chatsList.toMutableList()
        newList.add(position, chatInfo)
        updateChats(newList)
    }
    fun toggleSilentItem(position: Int) {
        if (position < 0 || position >= chatsList.size) return
        val newList = chatsList.toMutableList()
        newList[position] = newList[position].copy(isSilent = !newList[position].isSilent)
        updateChats(newList)
    }
    fun getSilentState(position: Int): Boolean {
        return chatsList.getOrNull(position)?.isSilent ?: false
    }

}

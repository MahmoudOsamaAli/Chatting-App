package com.example.chatbox.home.recyclerView

import MessagesDiffCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.databinding.ItemMessageBinding

class MessagesAdapter(
    private var messagesList: List<FakeData.Message>
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    // ViewHolder class to hold views for individual message items
    inner class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: FakeData.Message) {
            binding.messageUserName.text = message.name
            binding.messageLastMessage.text = message.content
            // Handle the silent status indicator
            binding.silentIcon.visibility = if (message.isSilent) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messagesList[position])
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    /**
     * Updates the list using DiffUtil for efficient changes.
     */
    fun updateMessages(newMessagesList: List<FakeData.Message>) {
        val diffCallback = MessagesDiffCallback(messagesList, newMessagesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        messagesList = newMessagesList
        diffResult.dispatchUpdatesTo(this)
    }

    // Helper function to remove an item from the list
    fun removeItem(position: Int): FakeData.Message? {
        if (position < 0 || position >= messagesList.size) return null
        val newList = messagesList.toMutableList()
        val removedItem = newList.removeAt(position)
        updateMessages(newList)
        return removedItem
    }

    // Helper function to restore an item in the list
    fun restoreItem(message: FakeData.Message, position: Int) {
        val newList = messagesList.toMutableList()
        newList.add(position, message)
        updateMessages(newList)
    }

    // Helper function to toggle silent status
    fun toggleSilentItem(position: Int) {
        if (position < 0 || position >= messagesList.size) return
        val newList = messagesList.toMutableList()
        newList[position] = newList[position].copy(isSilent = !newList[position].isSilent)
        updateMessages(newList)
    }

    // Helper function to get silent state of a specific item
    fun getSilentState(position: Int): Boolean {
        return messagesList.getOrNull(position)?.isSilent ?: false
    }
}

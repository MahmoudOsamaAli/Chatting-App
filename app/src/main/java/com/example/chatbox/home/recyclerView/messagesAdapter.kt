package com.example.chatbox.home.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.databinding.ItemMessageBinding

/**
 * Adapter for displaying messages in a RecyclerView.
 * It manages a list of messages and their silent statuses.
 */
class MessagesAdapter(
    private val messages: MutableList<FakeData.Message>, // List of messages
    private val silentStatus: MutableList<Boolean> // List of silent statuses for each message
) : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    /**
     * ViewHolder class for holding and binding message item views.
     */
    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind message data to the UI components.
         */
        fun bind(message: FakeData.Message, isSilent: Boolean) {
            binding.messageUserName.text = message.name // Set the message sender's name
            binding.messageLastMessage.text = message.content // Set the last message content

            // Update UI based on silent status
            binding.silentIcon.visibility = if (isSilent) View.VISIBLE else View.GONE

            // Set click listener for the silent icon to toggle its state
            binding.silentIcon.setOnClickListener {
                toggleSilentItem(adapterPosition) // Toggle silent state when clicked
            }
        }
    }

    /**
     * Create a new ViewHolder instance for the RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflate the item layout and create the ViewHolder
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    /**
     * Bind data to the ViewHolder for a specific position.
     */
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position] // Get the message at the current position
        holder.bind(message, silentStatus[position]) // Bind message and silent status to the ViewHolder
    }

    /**
     * Get the total number of items in the adapter.
     */
    override fun getItemCount(): Int = messages.size

    /**
     * Toggle the silent state for a specific message.
     */
    fun toggleSilentItem(position: Int) {
        // Ensure the position is valid before modifying
        if (position < 0 || position >= messages.size) return

        silentStatus[position] = !silentStatus[position] // Toggle the silent status
        val message = messages[position]
        message.isSilent = !message.isSilent // Update the message's silent state
        notifyItemChanged(position) // Notify the adapter that the item has changed
    }

    /**
     * Get the current silent state for a message.
     */
    fun getSilentState(position: Int): Boolean {
        // Ensure the position is valid before accessing the list
        if (position < 0 || position >= messages.size) {
            return false // Return a default value if the index is invalid
        }
        return messages[position].isSilent // Return the silent state of the message
    }

    /**
     * Remove an item at a specific position from the adapter.
     */
    fun removeItem(position: Int): FakeData.Message? {
        // Ensure the position is valid before removing
        if (position < 0 || position >= messages.size) return null

        // Remove the message and its silent status from the lists
        val removedMessage = messages.removeAt(position)
        silentStatus.removeAt(position) // Remove silent status for the deleted message

        notifyItemRemoved(position) // Notify adapter about the removed item
        return removedMessage // Return the removed message
    }

    /**
     * Restore a deleted message at a specific position in the adapter.
     */
    fun restoreItem(message: FakeData.Message, position: Int) {
        messages.add(position, message) // Add the message back to the list
        silentStatus.add(position, message.isSilent) // Restore its silent status
        notifyItemInserted(position) // Notify the adapter of the new item
    }
}

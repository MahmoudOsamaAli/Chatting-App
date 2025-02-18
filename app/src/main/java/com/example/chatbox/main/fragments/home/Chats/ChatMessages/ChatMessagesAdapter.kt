package com.example.chatbox.main.fragments.home.Chats.ChatMessages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.R
import com.example.chatbox.data.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatMessagesAdapter(
    private val currentUserId: String
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())


    companion object {
        const val VIEW_TYPE_SEND = 0
        const val VIEW_TYPE_RECEIVE = 1
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageRequest: TextView = itemView.findViewById(R.id.itemMessageRequest)
        val messageRequestTime: TextView = itemView.findViewById(R.id.itemMessageRequestTime)
    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageSent: TextView = itemView.findViewById(R.id.send_message)
        val messageSendingTime: TextView = itemView.findViewById(R.id.send_message_time)
        val messageState: ImageView = itemView.findViewById(R.id.ic_message_state)

    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)

        return if (message.senderId == currentUserId) VIEW_TYPE_SEND else VIEW_TYPE_RECEIVE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SEND) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_send_message, parent, false)
            SendViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_request_message, parent, false)
            RequestViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = getItem(position)
        val formattedTime = timeFormat.format(Date(currentMessage.timestamp))
        if (holder is SendViewHolder) {
            holder.messageSent.text = currentMessage.message
            holder.messageSendingTime.text = formattedTime
            if (currentMessage.isSeen) {
                holder.messageState.setImageResource(R.drawable.ic_message_seen)
            } else {
                holder.messageState.setImageResource(R.drawable.ic_message_unseen)
            }

        } else if (holder is RequestViewHolder) {
            holder.messageRequest.text = currentMessage.message
            holder.messageRequestTime.text = formattedTime
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.senderId == newItem.senderId
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}

package com.example.chatbox.main.fragments.home.Chats.ChatMessages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.R
import com.example.chatbox.data.FakeData

class ChatMessagesAdapter(
    private val chatInfoList: List<FakeData.ChatMessage>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SEND = 0
        const val VIEW_TYPE_RECEIVE = 1
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageRequest: TextView = itemView.findViewById(R.id.itemMessageRequest)
        val messageRequestTime: TextView = itemView.findViewById(R.id.itemMessageRequestTime)
    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageSent: TextView = itemView.findViewById(R.id.itemMessageSend)
        val messageSendingTime: TextView = itemView.findViewById(R.id.itemMessageSendTime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatInfoList[position].isCurrentUser) {
            VIEW_TYPE_SEND
        } else {
            VIEW_TYPE_RECEIVE
        }
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

    override fun getItemCount() = chatInfoList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = chatInfoList[position]
        if (holder is SendViewHolder) {
            holder.messageSent.text = currentMessage.text
            holder.messageSendingTime.text = currentMessage.time
        } else if (holder is RequestViewHolder) {
            holder.messageRequest.text = currentMessage.text
            holder.messageRequestTime.text = currentMessage.time
        }
    }
}

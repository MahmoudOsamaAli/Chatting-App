package com.example.chatbox.main.fragments.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.R


class ContactsAdapter(
    private val contactsList: List<Contact> ,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val CONTACT_USER = 0
        const val CONTACT_NOT_USER = 1
    }

     interface OnItemClickListener {
        fun onUserItemClick(contact:Contact,isContact: Boolean)
    }

    inner class ContactUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactNumber: TextView = itemView.findViewById(R.id.contact_number)
        val contactListenerArea:View = itemView.findViewById(R.id.contact_chat_area)
    }

    inner class ContactNotUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactNumber: TextView = itemView.findViewById(R.id.contact_number)
        val contactInviteIcon : ImageView = itemView.findViewById(R.id.contact_invite)
    }

    override fun getItemViewType(position: Int): Int {
        return if (contactsList[position].isUser) CONTACT_USER else CONTACT_NOT_USER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == CONTACT_USER) {
            R.layout.item_contact_user
        } else {
            R.layout.item_contact_non_user
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return if (viewType == CONTACT_USER) {
            ContactUserViewHolder(view)
        } else {
            ContactNotUserViewHolder(view)
        }
    }

    override fun getItemCount() = contactsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = contactsList[position]
        var isContactUser = false
        if (holder is ContactUserViewHolder) {
            holder.contactName.text = currentItem.contactName
            holder.contactNumber.text = currentItem.contactNumber
            holder.contactListenerArea.setOnClickListener {
                isContactUser = true
                listener.onUserItemClick(currentItem,isContactUser)
            }
        } else if (holder is ContactNotUserViewHolder) {
            holder.contactName.text = currentItem.contactName
            holder.contactNumber.text = currentItem.contactNumber
            holder.contactInviteIcon.setOnClickListener{
                isContactUser = false
                listener.onUserItemClick(currentItem,isContactUser)
            }

        }
    }

}

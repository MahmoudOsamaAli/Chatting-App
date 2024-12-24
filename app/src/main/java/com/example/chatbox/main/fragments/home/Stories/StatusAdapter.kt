package com.example.chatbox.main.fragments.home.Stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox.R
import com.example.chatbox.data.FakeData

class StatusAdapter(val statusInfoList: List<FakeData.StatusInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val MY_STORY = 0
        const val OTHER_STORIES = 1
    }

    inner class MyStoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    inner class OtherStoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.status_user_name)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) MY_STORY else OTHER_STORIES
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MY_STORY) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_story, parent, false)
            MyStoryViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_other_story, parent, false)
            OtherStoriesViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentStatus = statusInfoList[position]
        if (holder is OtherStoriesViewHolder) {
            holder.userName.text = currentStatus.userName
        }
    }

    override fun getItemCount() = statusInfoList.size
}

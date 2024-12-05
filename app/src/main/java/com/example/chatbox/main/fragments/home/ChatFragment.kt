package com.example.chatbox.main.fragments.home

import ChatMessagesAdapter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbox.R
import com.example.chatbox.data.FakeData
import com.example.chatbox.databinding.FragmentChatMessagesBinding

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatMessagesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleRecyclerView()
        getData()
        backToHome()
        switchToSendIcon()
    }

    private fun switchToSendIcon() {

        binding.chatEtNewMessage.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //blank
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // blank
            }

            override fun afterTextChanged(e: Editable?) {
                if(e.isNullOrEmpty()){
                    binding.chatIcSend.visibility = GONE
                    binding.chatIcMicRecord.visibility = View.VISIBLE
                    binding.chatIcGallery.visibility = View.VISIBLE
                } else {
                    binding.chatIcMicRecord.visibility = GONE
                    binding.chatIcGallery.visibility = GONE
                    binding.chatIcSend.visibility = View.VISIBLE

                }

            }
        })
    }

    private fun backToHome(){
        binding.chatIcBackArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    private fun getData() {
        val userName = arguments?.getString("userName")
        binding.chatUserName.text = userName
    }

    private fun handleRecyclerView() {
        val recyclerView = binding.chatRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ChatMessagesAdapter(FakeData().getChatMessages())
        recyclerView.adapter = adapter
    }

}
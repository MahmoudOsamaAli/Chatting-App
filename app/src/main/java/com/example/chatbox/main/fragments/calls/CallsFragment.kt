package com.example.chatbox.main.fragments.calls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatbox.R
import com.example.chatbox.databinding.FragmentCallsBinding

class CallsFragment : Fragment() {
    lateinit var binding: FragmentCallsBinding
    companion object{
        const val Tag = "CallsFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCallsBinding.inflate(inflater,container,false)
        return binding.root
    }

}
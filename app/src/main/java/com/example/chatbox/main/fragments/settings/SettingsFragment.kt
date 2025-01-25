package com.example.chatbox.main.fragments.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatbox.startup.StartupActivity
import com.example.chatbox.databinding.FragmentsSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    lateinit var binding : FragmentsSettingsBinding
    lateinit var auth : FirebaseAuth
    companion object{
        const val Tag = "SettingsFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentsSettingsBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.settingsIcLogout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this.requireContext(), StartupActivity::class.java))
            requireActivity().finish()
        }
    }
}
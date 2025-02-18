package com.example.chatbox.main.fragments.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatbox.startup.StartupActivity
import com.example.chatbox.databinding.FragmentSettingsBinding
import com.example.chatbox.repository.FireBaseAuthRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment() {
    private val dataBase = FirebaseDatabase.getInstance()
    private val userId = FireBaseAuthRepo.getUserUUID()
    private val userStateRef = dataBase.getReference("users").child(userId!!)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val formattedTime = timeFormat.format(Date())
    lateinit var binding: FragmentSettingsBinding
    lateinit var auth: FirebaseAuth

    companion object {
        const val Tag = "SettingsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        binding.settingsIcLogout.setOnClickListener {
            userStateRef.child("userState").setValue(formattedTime)


            auth.signOut()

            startActivity(Intent(requireContext(), StartupActivity::class.java))
            requireActivity().finish()
        }
    }
}

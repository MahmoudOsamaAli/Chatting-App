package com.example.chatbox.main.fragments.home.Chats.ChatMessages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.chatbox.R
import com.example.chatbox.databinding.FragmentUserInfoBinding



/**
 * A simple [Fragment] subclass.
 * Use the [UserInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserInfoFragment : Fragment(R.layout.fragment_user_info) {
    companion object{
        private const val USER_NAME = "userName"
        private const val USER_STATE = "userState"
        private const val USER_BIO = "userBio"
        private const val USER_PROFILE_PIC = "userProfilePic"
        private const val USER_PHONE_NUMBER = "userPhoneNumber"
        fun newInstance(name:String,profilePic:String,phoneNumber:String,state:String,bio:String):UserInfoFragment{
            val userInfoFragment = UserInfoFragment()
            val bundle = Bundle().apply {
                putString(USER_NAME,name)
                putString(USER_PROFILE_PIC,profilePic)
                putString(USER_PHONE_NUMBER,phoneNumber)
                putString(USER_STATE,state)
                putString(USER_BIO,bio)
            }
            userInfoFragment.arguments = bundle
            return userInfoFragment
        }
    }
    private lateinit var binding: FragmentUserInfoBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserInfoBinding.bind(requireView())

        val name = arguments?.getString(USER_NAME)?:""
        val profilePic = arguments?.getString(USER_PROFILE_PIC)?:""
        val phoneNumber = arguments?.getString(USER_PHONE_NUMBER)?:""
        val state = arguments?.getString(USER_STATE)?:""
        val bio = arguments?.getString(USER_BIO)?:""


        binding.apply {
            userInfoName.text = name
            userInfoFullName.text = name
            userInfoPhone.text = phoneNumber
            userInfoBio.text = bio
            userInfoState.text = state
            if (state == "Online")
                userInfoIcOnline.visibility = VISIBLE
            else {
                userInfoIcOnline.visibility = INVISIBLE
                userInfoTvState.visibility = VISIBLE
            }

        }
        binding.userInfoBackArrow.setOnClickListener{
            requireActivity().onBackPressed()
        }
        Glide.with(this)
            .load(profilePic)
            .placeholder(R.drawable.img_profile_empty)
            .into(binding.userInfoProfilePic)
    }

}
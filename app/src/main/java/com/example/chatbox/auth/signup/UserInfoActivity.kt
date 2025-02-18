package com.example.chatbox.auth.signup

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.chatbox.BaseActivity
import com.example.chatbox.databinding.ActivityUserInfoBinding
import com.example.chatbox.main.MainActivity
import com.example.chatbox.network.ServerCallBack
import com.example.chatbox.utils.Constants
import com.example.chatbox.utils.loadImageUrl
import com.example.chatbox.utils.showToast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class UserInfoActivity : BaseActivity() {

    private val viewModel: UserInfoViewModel by viewModels()
    private lateinit var binding: ActivityUserInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userinfoAddNewProfilePicture.setOnClickListener {
            openGallary()
        }

        binding.userinfoBtnJoin.setOnClickListener {
            viewModel.userFullName =
                "${binding.userinfoEdFirstName.text} ${binding.userinfoEdLastName.text}"
            viewModel.userBio = binding.userinfoEdBio.text.toString()
            saveUserToDB()
        }
    }

    private fun saveUserToDB() {
        viewModel.saveUserToDB().observe(this) {
            when (it.status) {
                ServerCallBack.Status.LOADING -> {
                    showDefaultLoading()
                }

                ServerCallBack.Status.SUCCESS -> {
                    hideDefaultLoading()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                ServerCallBack.Status.ERROR -> {
                    hideDefaultLoading()
                    showToast(it.message.orEmpty())
                }
            }
        }
    }

    private fun updateUserProfilePicture() {
        MainScope().launch {
            viewModel.uploadUserImage(this@UserInfoActivity).observe(this@UserInfoActivity) {
                when (it.status) {
                    ServerCallBack.Status.LOADING -> {
                        showDefaultLoading()
                    }

                    ServerCallBack.Status.SUCCESS -> {
                        hideDefaultLoading()
                        it.data?.data?.let { response ->
                            viewModel.userPictureUrl = response.link
                            loadImageUrl(viewModel.userPictureUrl!!,binding.userinfoImgProfilePicture)
                        }
                    }

                    ServerCallBack.Status.ERROR -> {
                        hideDefaultLoading()
                        showToast(it.message.orEmpty())
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constants.READ_IMAGE_REQUEST) {
            data?.data?.let { galleryUri ->
                viewModel.localPictureURI = galleryUri
                updateUserProfilePicture()
            }
        }
    }

    private fun openGallary() {
        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, Constants.READ_IMAGE_REQUEST)
    }


}
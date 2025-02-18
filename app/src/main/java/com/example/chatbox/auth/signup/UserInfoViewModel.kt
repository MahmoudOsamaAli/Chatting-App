package com.example.chatbox.auth.signup

import android.app.Activity
import android.app.Application
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.chatbox.data.User
import com.example.chatbox.data.response.DatabaseResponse
import com.example.chatbox.network.ServerCallBack
import com.example.chatbox.repository.FireBaseAuthRepo
import com.example.chatbox.repository.FirebaseDatabaseRepo
import com.example.chatbox.repository.ImgurRepo
import com.example.chatbox.utils.Constants
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserInfoViewModel(application: Application) : AndroidViewModel(application) {

    var userFullName: String = ""
    var userBio: String = ""
    var userPictureUrl: String = ""
    var localPictureURI: Uri? = null

    fun saveUserToDB(): MutableLiveData<ServerCallBack<DatabaseResponse>> {
        val user = User(
            userId = FireBaseAuthRepo.getUserUUID()!!,
            userName = userFullName,
            profilePicture = userPictureUrl,
            phoneNumber = FireBaseAuthRepo.getUserPhoneNumber()!!,
            bio = userBio

        )
        return FirebaseDatabaseRepo.saveUserToDB(user)
    }

    suspend fun uploadUserImage(context: Activity) = ImgurRepo.uploadImage(createMultipartBody(context))

    private suspend fun createMultipartBody(context: Activity) = withContext(Dispatchers.IO) {
        val photoFile = createImageFile(context)
        // Save the selected image to the file
        saveImageToFile(context, photoFile)
        val compressedImageFile = Compressor.compress(context, photoFile)

        val requestFile = RequestBody.create(Constants.MEDIA_INTENT_TYPE.toMediaTypeOrNull(), compressedImageFile)
        return@withContext MultipartBody.Part.createFormData("image", compressedImageFile.name, requestFile)
    }

    private fun createImageFile(context: Activity): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat(Constants.DATE_FORMATE_FOR_IMAGE_FILE, Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun saveImageToFile(activity: Activity, file: File) {
        try {
            localPictureURI?.let {
                val inputStream: InputStream? = activity.contentResolver.openInputStream(localPictureURI!!)
                val outputStream = FileOutputStream(file)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("updateUserProfilePicture", "saveImageToFile: ${e.message}")
        }
    }

}
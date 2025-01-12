package com.example.chatbox.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtil {

    fun checkPermission(
        activity: Activity, permission: String, requestID: Int,
        permissionListener: PermissionListener?
    ) {
        if (ContextCompat.checkSelfPermission(
                activity, permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(permission), requestID
            )
        } else {
            permissionListener?.permissionGranted()
        }
    }

    fun checkPermission(
        activity: Activity, permissions: Array<String>, requestID: Int,
        permissionListener: PermissionListener?
    ) {
        var permissionChecked = true

        for (i in permissions) {
            permissionChecked =
                permissionChecked && (ContextCompat.checkSelfPermission(activity, i)
                        != PackageManager.PERMISSION_GRANTED)
        }

        if (permissionChecked) {
            ActivityCompat.requestPermissions(
                activity, permissions, requestID
            )
        } else {
            permissionListener?.permissionGranted()
        }
    }

    fun checkPermission(
        fragment: Fragment, permission: String, requestID: Int,
        permissionListener: PermissionListener?
    ) {
        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(), permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            fragment.requestPermissions(
                arrayOf(permission), requestID
            )
        } else {
            permissionListener?.permissionGranted()
        }
    }

    fun onRequestPermissionsResult(
        requestID: Int,//this id passed to the fun above
        requestCode: Int,//this one come from the system callback
        permissions: Array<out String>,
        grantResults: IntArray,
        permissionListener: PermissionListener?
    ) {
        when (requestCode) {
            requestID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    permissionListener?.permissionGranted()
                } else if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
                    // permission denied, boo! Disable the
                    permissionListener?.permissionDenied()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    //************************************Permission listener***************************************
    interface PermissionListener {
        fun permissionGranted()
        fun permissionDenied()
    }
}
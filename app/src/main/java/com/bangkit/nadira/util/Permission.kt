package com.bangkit.nadira.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission  {

    companion object{
        const val CAMERA_CODE = 999

        fun isCameraGranted(context: Activity) : Boolean{
            return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED
        }
        fun grantCameraAccess(context: Activity) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_CODE
                )
            }
        }
    }

}
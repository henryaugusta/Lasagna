package com.feylabs.lasagna.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.feylabs.lasagna.view.report.user_input.UserTakePhotoActivity

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
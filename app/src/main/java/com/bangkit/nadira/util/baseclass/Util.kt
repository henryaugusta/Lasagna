package com.bangkit.nadira.util.baseclass

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS


class Util {

    companion object {
        fun setStatusBarLight(context: Activity) {
            val window: Window = context.window
            val decorView = window.decorView

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val wic: WindowInsetsController? = decorView.getWindowInsetsController()
                wic?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_STATUS_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}
package com.bangkit.nadira.view.bottom_sheet

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.arthurivanets.bottomsheets.BaseBottomSheet
import com.arthurivanets.bottomsheets.config.BaseConfig
import com.arthurivanets.bottomsheets.config.Config
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.OptionReportUserBinding

class OptionReportUserBottomSheet(
    hostActivity: Activity,
    config: BaseConfig = Config.Builder(hostActivity).build()
) : BaseBottomSheet(hostActivity, config) {

    lateinit var myVbind: OptionReportUserBinding

    override fun onCreateSheetContentView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.option_report_user, this, false)
        myVbind = OptionReportUserBinding.bind(view)
        Config.Builder(myVbind.root.context).dismissOnTouchOutside(false).build()


        return myVbind.root
    }


}
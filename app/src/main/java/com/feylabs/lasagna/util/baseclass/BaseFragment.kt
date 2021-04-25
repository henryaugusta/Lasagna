package com.feylabs.lasagna.util.baseclass

import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.feylabs.lasagna.R
import com.tapadoo.alerter.Alerter

open class BaseFragment : Fragment() {


    fun String.showLongToast(){
        Toast.makeText(requireContext(),this, Toast.LENGTH_LONG).show()
    }

    fun showSweetAlert(title:String,desc:String,color : Int){
        Alerter.create(requireActivity())
            .setTitle(title)
            .setText(desc)
            .setBackgroundColorRes(color) // or setBackgroundColorInt(Color.CYAN)
            .show()
    }


}
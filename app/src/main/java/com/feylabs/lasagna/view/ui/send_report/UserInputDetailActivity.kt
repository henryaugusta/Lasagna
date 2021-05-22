package com.feylabs.lasagna.view.ui.send_report

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityUserInputDetailBinding
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.DESC
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.LOC

class UserInputDetailActivity : AppCompatActivity() {



    val vbind by lazy {ActivityUserInputDetailBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        Util.setStatusBarLight(this)

        setUpToolbar()

        vbind.includeToolbar.myCustomToolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }


    }

    private fun goToReview(sDesc : String,sLoc:String) {
        Preference(this).save(DESC,sDesc)
        Preference(this).save(LOC,sLoc)
        startActivity(Intent(this, UserReviewBeforeInputActivity::class.java))
    }

    private fun setUpToolbar() {
        vbind.includeToolbar.myCustomToolbar.apply {
            inflateMenu(R.menu.menu_input_report)
            title = "Buat Laporan Baru"
            setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.action_done -> {
                        checkIfDone()
                    }
                }

                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun checkIfDone() {
        var isDone = true
        val sDesc = vbind.etDetailKejadian.text.toString()
        val sLoc = vbind.etDetailAlamat.text.toString()

        if (sDesc.length<10){
            isDone=false
            vbind.etDetailKejadian.error="Minimal 10 Karakter"
        }
        if (sLoc.length<10){
            isDone=false
            vbind.etDetailAlamat.error="Minimal 10 Karakter"
        }

        if (isDone){
            goToReview(sDesc,sLoc)
        }
    }
}
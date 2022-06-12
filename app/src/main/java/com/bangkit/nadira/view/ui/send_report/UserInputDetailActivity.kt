package com.bangkit.nadira.view.ui.send_report

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.ActivityUserInputDetailBinding
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.baseclass.Util
import com.bangkit.nadira.view.ui.send_report.UserReviewBeforeInputActivity.Companion.DESC
import com.bangkit.nadira.view.ui.send_report.UserReviewBeforeInputActivity.Companion.KERUSAKAN_BANGUNAN
import com.bangkit.nadira.view.ui.send_report.UserReviewBeforeInputActivity.Companion.KERUSAKAN_LAIN
import com.bangkit.nadira.view.ui.send_report.UserReviewBeforeInputActivity.Companion.KONDISI_KORBA
import com.bangkit.nadira.view.ui.send_report.UserReviewBeforeInputActivity.Companion.KORBAN_JIWA
import com.bangkit.nadira.view.ui.send_report.UserReviewBeforeInputActivity.Companion.LOC
import com.google.android.material.textfield.TextInputLayout

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

        val kondisiKorban = vbind.textFieldKondisiKorban.getText()
        val korbanJiwa = vbind.textFieldKorbanJiwa.getText()
        val kerusakanLain = vbind.textFieldKerusakanLain.getText()
        val kerusakanBangunan = vbind.textFieldKerusakanBangunan.getText()

        Preference(this).apply {
            save(KERUSAKAN_BANGUNAN,kerusakanBangunan)
            save(KERUSAKAN_LAIN,kerusakanLain)
            save(KORBAN_JIWA,korbanJiwa)
            save(DESC,sDesc)
            save(KONDISI_KORBA,kondisiKorban)
            save(LOC,sLoc)
        }
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

    private fun TextInputLayout.getText(): String {
        return this.editText?.text.toString() ?: ""
    }

}


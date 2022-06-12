package com.bangkit.nadira.view.ui.send_report

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.ActivityReviewBeforeInputBinding
import com.bangkit.nadira.data.model.api.SendReportModel
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const.USER_ID
import com.bangkit.nadira.util.baseclass.BaseActivity
import com.bangkit.nadira.view.MainMenuUserActivity
import com.bangkit.nadira.viewmodel.UserReportViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File

class UserReviewBeforeInputActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    companion object {
        const val LAT = "LAT"
        const val LONG = "LONG"
        const val CATEGORY_ID = "CATEGORY_ID"
        const val CATEGORY_IMAGE = "CATEGORY_IMAGE"
        const val CATEGORY_NAME = "CATEGORY_NAME"
        const val DESC = "DESCRIPTION"
        const val LOC = "LOCSTION"
        const val IMAGE_URI = "LOxxCssSTION"

        const val TANGGAL_KEJADIAN = "LOxxCssSTION99l2"
        const val WAKTU_KEJADIAN = "LOxxCssSTIONgwgrc13"
        const val PENYEBAB_KEJADIAN = "LOxxCssSTION0t525"
        const val KERUSAKAN_BANGUNAN = "LOxxCssSThr44ION"
        const val KERUSAKAN_LAIN = "LOxxCssSTIOvwN"
        const val KORBAN_JIWA = "LOxxCssSTIOewvgeN"
        const val KONDISI_KORBA = "kondisi_kreonassSTION"
    }

    val reportViewModel by lazy { ViewModelProvider(this).get(UserReportViewModel::class.java) }

    var myLatitude = 0.0
    var myLongitude = 0.0

    val vbind by lazy { ActivityReviewBeforeInputBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        myLatitude = Preference(this).getPrefString(LAT).toString().toDouble()
        myLongitude = Preference(this).getPrefString(LONG).toString().toDouble()
        val myImage = Preference(this).getPrefString(IMAGE_URI)
        val myLoc = Preference(this).getPrefString(LOC)
        val myDesc = Preference(this).getPrefString(DESC)
        val myCategoryImage = Preference(this).getPrefString(CATEGORY_IMAGE)

        vbind.ivReportImg.setImageURI(myImage?.toUri()?.path?.toUri())
        vbind.etDetailAlamat.text = myLoc.toString()
        vbind.etDetailKeterangan.text = myDesc

        vbind.tvWaktuKejadian.text = Preference(this).getPrefString(WAKTU_KEJADIAN)
        vbind.tvPenyebab.text = Preference(this).getPrefString(PENYEBAB_KEJADIAN)
        vbind.tvKerusakanBangunan.text=Preference(this).getPrefString(KERUSAKAN_BANGUNAN)
        vbind.tvKerusakanLain.text=Preference(this).getPrefString(KERUSAKAN_LAIN)
        vbind.tvKorbanJiwa.text=Preference(this).getPrefString(KORBAN_JIWA)
        vbind.tvKondisiKorban.text=Preference(this).getPrefString(KONDISI_KORBA)

        Picasso.get()
            .load(myCategoryImage)
            .into(vbind.imageCategory)

        vbind.textViewCategory.text = Preference(this).getPrefString(CATEGORY_NAME)
        Timber.d("NAMA CATEGORY2 ${Preference(this).getPrefString(CATEGORY_NAME)}")

        Preference(this).apply {

            vbind.btnSendReport.setOnClickListener {
                val reportModel = SendReportModel(
                    id_people = getPrefString(USER_ID).toString(),
                    id_category = getPrefInt(CATEGORY_ID).toString(),
                    is_public = "1",
                    detail_kejadian = getPrefString(DESC).toString(),
                    detail_alamat = getPrefString(LOC).toString(),
                    photo = File(myImage?.toUri()?.path),
                    lat = myLatitude,
                    long =  myLongitude,
                    status = "0",
                    waktu_kejadian = getPrefString(WAKTU_KEJADIAN),
                    kerusakan_bangunan = getPrefString(KERUSAKAN_BANGUNAN),
                    kerusakan_lain = getPrefString(KERUSAKAN_LAIN),
                    kondisi_korban = getPrefString(KONDISI_KORBA),
                    korban_jiwa = getPrefString(KORBAN_JIWA),
                    penyebab_bencana = getPrefString(PENYEBAB_KEJADIAN)
                )
                reportViewModel.sendReportModel(reportModel)
            }
        }


        reportViewModel.sendReportStatus.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("uploadReport: loading")
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("uploadReport: error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("uploadReport: success")
                    showSweetAlert("Success","Berhasil Mengirim Report",R.color.xdGreen)
                    GlobalScope.launch {
                        delay(1000)
                        withContext(Dispatchers.Main){
                            finish()
                          startActivity(Intent(applicationContext,MainMenuUserActivity::class.java))
                        }
                    }
                }

            }
        })

    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val location = LatLng(myLatitude, myLongitude)
        mMap.addMarker(MarkerOptions().position(location).title("Lokasi Laporan"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))

    }
}
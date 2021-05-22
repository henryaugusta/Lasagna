package com.feylabs.lasagna.view.ui.send_report

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityReviewBeforeInputBinding
import com.feylabs.lasagna.data.model.api.SendReportModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.const.USER_ID
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.view.MainMenuUserActivity
import com.feylabs.lasagna.viewmodel.UserReportViewModel
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
                    status = "0"
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
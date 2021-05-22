package com.feylabs.lasagna.view.ui.report

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityDetailReportBinding
import com.feylabs.lasagna.data.model.ReportDetailModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.util.networking.Endpoint
import com.feylabs.lasagna.viewmodel.UserReportViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

class DetailReportActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val vbind by lazy { ActivityDetailReportBinding.inflate(layoutInflater) }
    val viewModel by lazy { ViewModelProvider(this).get(UserReportViewModel::class.java) }

    var reportID = ""

    lateinit var detailModel: ReportDetailModel

    companion object {
        const val REPORT_ID = "report_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        Util.setStatusBarLight(this)

        vbind.includeToolbar.myCustomToolbar.title="Detail laporan"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        vbind.includeLoading.loadingRoot.setVisible()

        reportID = intent.getStringExtra(REPORT_ID).toString()


        if (reportID != "")
            viewModel.getDetailReport(reportID)
        else {
            "Data Tidak Valid".showLongToast()
            onBackPressed()
        }

        setUpObserver()


    }

    private fun setUpObserver() {
        viewModel.statusDetailReport.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    showSweetAlert("Error", it.message.toString(), R.color.xdRed)
                    Timber.d("myReportFragment: ->failed fetch report")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("myReportFragment: ->success fetch report")
                    it.data?.let {
                        detailModel = it
                        setLayout(detailModel.report)
                    }

                }
                else -> {
                }
            }
        })


    }

    private fun setLayout(model: ReportDetailModel.Report) {
        vbind.apply {
            model.apply {
                vbind.etDetailAlamat.text = detail_alamat
                vbind.etDetailKeterangan.text = detail_kejadian
                vbind.textViewCategory.text = category.category_name

                setLocation(model.lat.toDouble(),model.long.toDouble(),model.detail_alamat)

            }
        }

        Glide
            .with(vbind.root)
            .load(Endpoint.REAL_URL + model.category.photo_path)
            .centerCrop()
            .skipMemoryCache(true)
            .dontAnimate()
            .thumbnail(Glide.with(this).load(R.raw.loading2))
            .placeholder(R.drawable.ic_loading_small_1)
            .into(vbind.imageCategory)

        Timber.d("category image url : ${Endpoint.REAL_URL}${model.category.photo_path}")
        Glide
            .with(vbind.root)
            .load(Endpoint.REAL_URL + model.photo_path)
            .centerCrop()
            .placeholder(R.drawable.dark_placeholder)
            .thumbnail(Glide.with(this).load(R.raw.loading2))
            .skipMemoryCache(true)
            .dontAnimate()
            .placeholder(R.drawable.ic_loading_small_1)
            .into(vbind.ivReportImg)

    }

    private fun setLocation(lat: Double, long: Double,title:String) {
        val location = LatLng(lat,long)
        mMap.addMarker(MarkerOptions().position(location).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }


}
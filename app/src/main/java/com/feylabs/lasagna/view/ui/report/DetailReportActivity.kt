package com.feylabs.lasagna.view.ui.report

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.adapter.ResponseAdapter
import com.feylabs.lasagna.data.model.ReportDetailModel
import com.feylabs.lasagna.databinding.ActivityDetailReportBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.const
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.util.networking.Endpoint
import com.feylabs.lasagna.view.ui.proceed.EditReportActivity
import com.feylabs.lasagna.view.ui.proceed.EditReportActivity.Companion.REPORT_AIDI
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

    val adapterResponse by lazy { ResponseAdapter() }

    var reportID = ""

    lateinit var detailModel: ReportDetailModel

    companion object {
        const val REPORT_ID = "report_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        Util.setStatusBarLight(this)

        vbind.includeToolbar.myCustomToolbar.title = "Detail laporan"


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        vbind.includeLoading.loadingRoot.setVisible()



        reportID = intent.getStringExtra(REPORT_ID).toString()

        vbind.includeToolbar.myCustomToolbar.setOnClickListener {
            finish()
            onBackPressed()
        }

        if (reportID != "")
            viewModel.getDetailReport(reportID)
        else {
            "Data Tidak Valid".showLongToast()
            onBackPressed()
        }

        if (Preference(this).getPrefString(const.USER_TYPE) != "admin") {
            vbind.btnChangeStatus.visibility = View.GONE
        }

        vbind.btnChangeStatus.setOnClickListener {
            startActivityForResult(
                Intent(this, EditReportActivity::class.java).putExtra(
                    REPORT_AIDI,
                    reportID
                ), 1500
            )
        }


        setupReyclerview()
        setupAdapter()
        setUpObserver()

    }

    private fun setupAdapter() {
        adapterResponse.setInterface(object : ResponseAdapter.ResponseInterface {
            override fun onclick(model: ReportDetailModel.Report.Response) {
                model.updatedAt.showLongToast()
            }
        })
    }

    private fun setupReyclerview() {
        vbind.rvResponses.apply {
            adapter = adapterResponse
            layoutManager = LinearLayoutManager(this@DetailReportActivity)
        }
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

                    adapterResponse.setData(it.data?.report?.response?.toMutableList()!!)
                    adapterResponse.notifyDataSetChanged()

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
                vbind.etDetailAlamat.text = this.detailAlamat
                vbind.etDetailKeterangan.text = this.detailKejadian
                vbind.textViewCategory.text = category.categoryName

                setLocation(model.lat.toDouble(), model.long.toDouble(), model.detailAlamat)

            }
        }

        Glide
            .with(vbind.root)
            .load(Endpoint.REAL_URL + model.category.photoPath)
            .centerCrop()
            .skipMemoryCache(true)
            .dontAnimate()
            .thumbnail(Glide.with(this).load(R.raw.loading2))
            .placeholder(R.drawable.ic_loading_small_1)
            .into(vbind.imageCategory)

        Timber.d("category image url : ${Endpoint.REAL_URL}${model.category.photoPath}")
        Glide
            .with(vbind.root)
            .load(Endpoint.REAL_URL + model.photoPath)
            .centerCrop()
            .placeholder(R.drawable.dark_placeholder)
            .thumbnail(Glide.with(this).load(R.raw.loading2))
            .skipMemoryCache(true)
            .dontAnimate()
            .placeholder(R.drawable.ic_loading_small_1)
            .into(vbind.ivReportImg)

    }

    private fun setLocation(lat: Double, long: Double, title: String) {
        val location = LatLng(lat, long)
        mMap.addMarker(MarkerOptions().position(location).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1500) {
            if (resultCode == RESULT_OK) {
                val reportID = data?.getStringExtra(REPORT_ID)
                if (reportID != "")
                    viewModel.getDetailReport(reportID.toString())
                else {
                    "Data Tidak Valid".showLongToast()
                    onBackPressed()
                }
            }



        }
    }


}
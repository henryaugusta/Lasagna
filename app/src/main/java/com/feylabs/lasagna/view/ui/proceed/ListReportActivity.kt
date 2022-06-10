package com.feylabs.lasagna.view.ui.proceed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.adapter.HospitalAdapter
import com.feylabs.lasagna.adapter.ReportAdapter
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.databinding.ActivityListHospitalBinding
import com.feylabs.lasagna.databinding.MapInfoHospitalBinding
import com.feylabs.lasagna.data.model.api.HospitalModel
import com.feylabs.lasagna.data.model.api.ReportGetByUserModel
import com.feylabs.lasagna.databinding.ActivityListReportBinding
import com.feylabs.lasagna.databinding.MapInfoReportBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.const
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.view.bottom_sheet.OptionReportUserBottomSheet
import com.feylabs.lasagna.view.ui.report.DetailReportActivity
import com.feylabs.lasagna.view.ui.report.DetailReportActivity.Companion.REPORT_ID
import com.feylabs.lasagna.viewmodel.UserReportViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_list_hospital.view.*


class ListReportActivity : BaseActivity(), OnMapReadyCallback {


    val vbind by lazy { ActivityListReportBinding.inflate(layoutInflater) }
    val reportAdapter by lazy { ReportAdapter() }
    val userReportViewModel by lazy { ViewModelProvider(this).get(UserReportViewModel::class.java) }

    val optionUserSheet by lazy { OptionReportUserBottomSheet(this) }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    lateinit var modelForMarker: ReportGetByUserModel.Report.Data
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationManager: LocationManager


    private lateinit var mMap: GoogleMap
    private lateinit var mMapAll: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        Util.setStatusBarLight(this)

        val remoteDataSource = RemoteDataSource()
        val repository = LasagnaRepository(remoteDataSource)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        userReportViewModel.getAllReport()

        setRecyclerView()
        setUpAdapter()

        observe()

        getLastKnownLocation()


        vbind.srl.setOnRefreshListener {
            vbind.srl.isRefreshing = false
            userReportViewModel.getAllReport()
        }

        val mapFragment1 = supportFragmentManager
            .findFragmentById(R.id.mapping_report) as SupportMapFragment
        mapFragment1.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(p0: GoogleMap) {
                mMapAll = p0

                mMapAll.setOnMarkerClickListener { zaf ->
                    zaf.showInfoWindow()
                    true
                }

                mMapAll.setOnInfoWindowClickListener {
                    val model = it.tag as ReportGetByUserModel.Report.Data
                }

                mMapAll.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {

                    override fun getInfoWindow(p0: Marker): View? {
                        return null;
                    }

                    override fun getInfoContents(p0: Marker): View {
                        val v = MapInfoReportBinding.bind(
                            layoutInflater.inflate(
                                R.layout.map_info_report,
                                null
                            )
                        )

                        mMapAll.moveCamera(CameraUpdateFactory.newLatLng(p0.position))

                        val model = p0.tag as  ReportGetByUserModel.Report.Data
                        v.title.text = model.category.category_name
                        v.address.text = model.detail_alamat
                        v.etDetailKejadian.text=model.detail_kejadian
                        v.statusDesc.text = model.status_desc


                        return v.root
                    }

                })
            }

        })


    }

    private fun setUpAdapter() {
        reportAdapter.setInterface(object : ReportAdapter.ReportAdapterInterface {
            override fun onclick(model: ReportGetByUserModel.Report.Data) {
                optionUserSheet.show()

                optionUserSheet.myVbind.apply {
                    labelCategory.text = model.category.category_name
                    labelDetail.text = model.detail_kejadian
                    labelDetail.maxLines = 3
                    labelDetail.ellipsize = TextUtils.TruncateAt.MARQUEE
                    labelLocation.text = model.detail_alamat

                    if (Preference(this@ListReportActivity).getPrefString(const.USER_TYPE) == "admin") {
                        btnCancelReport.visibility=View.GONE
                    }

                    if(model.id_people.toString() != Preference(this@ListReportActivity).getPrefString(const.USER_ID)){
                        btnCancelReport.visibility=View.GONE
                    }

                    btnSeeDetail.setOnClickListener {
                        "Lihat Detail".showLongToast()
                        startActivity(
                            Intent(this@ListReportActivity, DetailReportActivity::class.java)
                                .putExtra(REPORT_ID, model.id.toString())
                        )
                    }

                    btnCancelReport.setOnClickListener {
                        userReportViewModel.deleteReport(model.id.toString())
                        "Batalkan Laporan".showLongToast()
                        if (model.status.toInt() == 0) {
                            optionUserSheet.dismiss(true)
                        } else {
                            "Laporan Yang Sudah Diproses Tidak Dapat Dibatalkan".showLongToast()
                        }
                    }
                }
            }
        })

    }


    private fun observe() {
        userReportViewModel.reportByUser.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.report?.data?.forEach { cur ->
                        modelForMarker = cur
                        mMapAll.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    cur.lat.toDouble(),
                                    cur.long.toDouble()
                                )
                            )
                        )?.tag = modelForMarker
                    }

                    Log.d("list report activity", it.data.toString())
                    it.data?.report?.data?.toMutableList()
                        ?.let { it1 -> reportAdapter.setData(it1) }
                    reportAdapter.notifyDataSetChanged()
                    vbind.includeLoading.loadingRoot.setGone()
                }
                is Resource.Error -> {
                    Log.d("hospital_activity", "error ${it.message}")
                    vbind.includeLoading.loadingRoot.setGone()
                    showSweetAlert("Error", it.message.toString(), R.color.colorRedPastel)
                }
                is Resource.Loading -> {
                    Log.d("hospital_activity", "loading")
                    vbind.includeLoading.loadingRoot.setVisible()
                }
                else -> {
                    Log.d("hospital_activity", "neutral")
                }
            }
        })
    }

    private fun setRecyclerView() {
        vbind.rvReport.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@ListReportActivity, 2)
            adapter = reportAdapter
        }
    }

    private fun refreshMap(lat: Double = 31.3547, long: Double = 34.3088, title: String) {
        val location = LatLng(lat, long)
        mMap.addMarker(MarkerOptions().position(location).title(title))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f))
    }

    @SuppressLint("MissingPermission")
    //Permission Already Checked at Parent Activity
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    mMapAll.clear()
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    val myLocation = userLatLng
                    mMapAll.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                    mMapAll.animateCamera(CameraUpdateFactory.zoomTo(11.0f))

                }
            }
    }

}
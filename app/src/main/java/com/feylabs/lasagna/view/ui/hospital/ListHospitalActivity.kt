package com.feylabs.lasagna.view.ui.hospital

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.adapter.HospitalAdapter
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.model.api.HospitalModel
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.databinding.ActivityListHospitalBinding
import com.feylabs.lasagna.databinding.MapInfoHospitalBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.const
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.baseclass.Util
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


class ListHospitalActivity : BaseActivity(), OnMapReadyCallback {


    val vbind by lazy { ActivityListHospitalBinding.inflate(layoutInflater) }
    val hospitalAdapter by lazy { HospitalAdapter() }
    val viewModelMap by lazy { ViewModelProvider(this).get(HospitalViewModel::class.java) }

    lateinit var modelForMarker: HospitalModel.Data
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationManager: LocationManager


    private lateinit var mMap: GoogleMap
    private lateinit var mMapAll: GoogleMap

    override fun onStart() {
        super.onStart()
        observeHospital()
    }

    lateinit var viewmodel: HospitalViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        Util.setStatusBarLight(this)
        vbind.includeLoading.loadingRoot.setVisible()

        val remoteDataSource = RemoteDataSource()
        val repository = LasagnaRepository(remoteDataSource)
        val factory = HospitalViewModelFactory(repository)
        viewmodel = ViewModelProvider(this, factory).get(HospitalViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        setRecyclerView()
        setUpAdapter()
        observeHospital()

        getLastKnownLocation()


        vbind.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddHospitalActivity::class.java))
        }

        vbind.srl.setOnRefreshListener {
            observeHospital()
            vbind.includeLoading.loadingRoot.setVisible()
            vbind.srl.isRefreshing = false
        }

        val mapFragment1 = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment1.getMapAsync(this)

        val mapFragment2 = supportFragmentManager
            .findFragmentById(R.id.mapping_hospital) as SupportMapFragment

        mapFragment2.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(p0: GoogleMap) {
                mMapAll = p0

                mMapAll.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                    override fun onMarkerClick(p0: Marker): Boolean {
                        p0.showInfoWindow()
                        return true
                    }

                })

                mMapAll.setOnInfoWindowClickListener {
                    val model = it.tag as HospitalModel.Data
                    hospitalItemOnClick(model)
                }

                mMapAll.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                    override fun getInfoWindow(p0: Marker): View? {
                        return null;
                    }

                    override fun getInfoContents(p0: Marker): View? {
                        val v = MapInfoHospitalBinding.bind(
                            layoutInflater.inflate(
                                R.layout.map_info_hospital,
                                null
                            )
                        )

                        mMapAll.moveCamera(CameraUpdateFactory.newLatLng(p0.position))

                        val model = p0.tag as HospitalModel.Data
                        v.title.text = model.name
                        v.address.text = model.alamat
                        v.fasilitas.text = model.fasilitas
                        v.operasional.text = model.operasional


                        return v.root
                    }

                })
            }

        })

        setBottomSheet()

    }

    private fun hospitalItemOnClick(model: HospitalModel.Data) {

        viewModelMap.hospitalModel.value = model
        viewModelMap.vmLoc.value = LatLng(model.lat.toDouble(), model.long.toDouble())
        refreshMap(model.lat.toDouble(), model.long.toDouble(), model.alamat)

        showBottomSheet()
        vbind.includeHospitalDetail.btnEdit.setOnClickListener {
            dismissBottomSheet()
            "Edit Data".showLongToast()
            startActivity(
                Intent(applicationContext, AddHospitalActivity::class.java)
                    .putExtra(AddHospitalActivity.ID_HOSPITAL, model.id.toString())
            )
        }

        vbind.includeHospitalDetail.btnDirection.setOnClickListener {
            val lat = model.lat
            val long = model.long
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:<$lat>,<$long>?q=<$lat>,<$long>(${model.name})&iwloc=A&hl=es")
            )
            startActivity(intent)
        }

        vbind.includeHospitalDetail.apply {

            btnCallAmbulance.setOnClickListener {
                contact(model.kontak_ambulance)
            }

            btnCall.setOnClickListener {
                contact(model.kontak_rs)
            }

            labelAddress.text = model.alamat
            labelDeskripsi.text = model.deskripsi
            labelRsName.text = model.name
            labelFacility.text = model.fasilitas
            labelContAmbulance.text = model.kontak_rs
            labelContRs.text = model.kontak_ambulance

            if (Preference(this@ListHospitalActivity).getPrefString(const.USER_TYPE) != "admin") {
                elenAdmin.visibility=View.GONE
            }

            btnCloseDetail.setOnClickListener {
                dismissBottomSheet()
            }

            btnDelete.setOnClickListener {
                dismissBottomSheet()
                viewmodel.deleteHospital(model.id.toString())
                    .observe(this@ListHospitalActivity,
                        Observer {
                            when (it) {
                                is Resource.Success -> {
                                    observeHospital()
                                    vbind.includeLoading.loadingRoot.setGone()
                                    showSweetAlert(
                                        "Success",
                                        it.data?.message.toString(),
                                        R.color.colorGiok
                                    )
                                }
                                is Resource.Error -> {
                                    vbind.includeLoading.loadingRoot.setGone()
                                    showSweetAlert(
                                        "Error",
                                        it.message.toString(),
                                        R.color.bootstrapRed
                                    )
                                }
                                is Resource.Loading -> {
                                    vbind.includeLoading.loadingRoot.setVisible()
                                }
                                else -> {
                                }
                            }
                        })
            }

            Glide
                .with(vbind.root)
                .load(model.real_photo_path)
                .centerCrop()
                .skipMemoryCache(true)
                .dontAnimate()
                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                .placeholder(R.drawable.ic_loading_small_1)
                .into(vbind.includeHospitalDetail.ivImage)
        }
    }

    fun contact(number:String){
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.withAppendedPath(
            ContactsContract.Contacts.CONTENT_URI,
           (number)
        )
        intent.data = uri
        startActivity(intent)
    }

    private fun setBottomSheet() {
        vbind.includeHospitalDetail.let { v ->
            v.btnCloseDetail.setOnClickListener {
                v.root.visibility = View.GONE
                v.root.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_gone)
            }
        }
    }

    private fun showBottomSheet() {
        vbind.includeHospitalDetail.let { v ->
            v.root.visibility = View.VISIBLE
            v.root.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_appear)
        }
    }

    private fun dismissBottomSheet() {
        vbind.includeHospitalDetail.let { v ->
            v.root.visibility = View.GONE
            v.root.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_gone)
        }
    }

    private fun setUpAdapter() {
        hospitalAdapter.setInterface(object : HospitalAdapter.HospitalAdapterInterface {
            override fun onclick(model: HospitalModel.Data) {
                hospitalItemOnClick(model)
            }

        })
    }

    private fun observeHospital() {
        viewmodel.getHospital().observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.forEach { cur ->
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

                    Log.d("hospital_activity", it.data.toString())
                    it.data?.toMutableList()?.let { it1 -> hospitalAdapter.setData(it1) }
                    hospitalAdapter.notifyDataSetChanged()
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
        vbind.rvHospital.apply {
            rv_hospital.setHasFixedSize(true)
            rv_hospital.layoutManager = LinearLayoutManager(this@ListHospitalActivity)
            rv_hospital.adapter = hospitalAdapter
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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
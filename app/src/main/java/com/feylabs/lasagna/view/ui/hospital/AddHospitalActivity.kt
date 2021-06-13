package com.feylabs.lasagna.view.ui.hospital

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.databinding.ActivityAddHospitalBinding
import com.feylabs.lasagna.data.model.SendCreateHospitalModel
import com.feylabs.lasagna.data.model.api.HospitalModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.viewmodel.InputReportViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File

@Suppress("SENSELESS_COMPARISON")
class AddHospitalActivity : BaseActivity(), OnMapReadyCallback {

    val vbind by lazy { ActivityAddHospitalBinding.inflate(layoutInflater) }
    val viewModelInputReport by lazy { ViewModelProvider(this).get(InputReportViewModel::class.java) }

    lateinit var viewmodelHospital: HospitalViewModel
    var imageHospital = ""

    var myLong: Double? = null
    var myLat: Double? = null

    lateinit var editObserver: Observer<Resource<String>>
    lateinit var createObserver: Observer<Resource<String>>


    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
        const val ID_HOSPITAL = "id_hospital"
    }

    var id = ""
    var sendAlamat = ""
    var sendName = ""
    var sendDesc = ""
    var sendOperasional = ""
    var sendContactRS = ""
    var sendContactAmbulance = ""
    var sendFasilitas = ""

    lateinit var hospitalModel: HospitalModel.Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        Util.setStatusBarLight(this)

        val factory = HospitalViewModelFactory(LasagnaRepository(RemoteDataSource()))
        viewmodelHospital = ViewModelProvider(this, factory).get(HospitalViewModel::class.java)

        observerVariableBinding()
        id = intent.getStringExtra(ID_HOSPITAL).toString()
        if (intent.hasExtra(ID_HOSPITAL)) {
            "Edit Data RS".showLongToast()
            viewmodelHospital.getHospitalDetail(id)
            observeHospitalDetail()
        }

        setupSaveButton()

        val fm = supportFragmentManager

        fm.beginTransaction()
            .add(R.id.containerMap, HospitalMapFragment())
            .commit()

        setUpMap()

        vbind.btnChangePhoto.setOnClickListener {
            initPhoto()
        }

    }

    private fun observerVariableBinding() {
        editObserver = Observer {
            when (it) {
                is Resource.Success -> {
                    viewmodelHospital.getHospitalDetail(id)
                    observeHospitalDetail()
                    vbind.includeLoadingFull.loadingRoot.setGone()
                    showSweetAlert(
                        "Success",
                        "Berhasil Mengupdate Rumah Sakit",
                        R.color.xdGreen
                    )
                }
                is Resource.Loading -> {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert(
                        "Error",
                        "Gagal Mengupdate Data Rumah Sakit",
                        R.color.xdRed
                    )
                    vbind.includeLoadingFull.loadingRoot.setGone()
                }
                else -> {
                }
            }
        }

        createObserver = Observer {
            when (it) {
                is Resource.Success -> {
                    vbind.includeLoadingFull.loadingRoot.setGone()
                    showSweetAlert(
                        "Success",
                        "Berhasil Menambah Data Rumah Sakit",
                        R.color.xdGreen
                    )
                    "Berhasil Menambah Rumah Sakit".showLongToast()
                    finish()
                    super.onBackPressed()
                }
                is Resource.Loading -> {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert(
                        "Error",
                        "Gagal Menambah Data Rumah Sakit",
                        R.color.xdRed
                    )
                    vbind.includeLoadingFull.loadingRoot.setGone()
                }
                else -> { }
            }
        }

        viewmodelHospital.vmLoc.observe(this, Observer {
            myLat = it.latitude
            myLong = it.longitude
        })

    }
    private fun observeHospitalDetail() {
        viewmodelHospital.getHospitalDetail(id).observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    vbind.includeLoadingFull.loadingRoot.setGone()
                    if (it.data != null) {
                        hospitalModel = it.data
                    }

                    myLat = it.data?.lat?.toDouble()
                    myLong = it.data?.lat?.toDouble()
                    vbind.apply {
                        it.data.apply {

                            if (this?.lat!=null){
                                viewmodelHospital.vmLoc.value=LatLng(this.lat.toDouble(),
                                    this.long.toDouble()
                                )
                            }



                            etName.setText(this?.name)
                            etFasilitas.setText(this?.fasilitas)
                            etContactHospital.setText(this?.kontak_rs)
                            etContactAmbulance.setText(this?.kontak_ambulance)
                            etOperasional.setText(this?.operasional)
                            etDeskripsi.setText(this?.deskripsi)
                            etAlamat.setText(this?.alamat)

                            Glide
                                .with(vbind.root)
                                .load(this?.real_photo_path)
                                .centerCrop()
                                .skipMemoryCache(true)
                                .dontAnimate()
                                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                                .placeholder(R.drawable.ic_loading_small_1)
                                .into(ivPict)
                        }
                    }
                }

                is Resource.Loading -> {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    vbind.includeLoadingFull.loadingRoot.setGone()
                    "Gagal Menampilkan RS".showLongToast()
                }
                else -> {
                }
            }

        })
    }

    private fun setupSaveButton() {
        vbind.btnSaveChanges.setOnClickListener {
            vbind.apply {
                sendAlamat = etAlamat.text.toString()
                sendName = etName.text.toString()
                sendDesc = etDeskripsi.text.toString()
                sendOperasional = etOperasional.text.toString()
                sendContactRS = etContactHospital.text.toString()
                sendContactAmbulance = etContactAmbulance.text.toString()
                sendFasilitas = etFasilitas.text.toString()
            }
            var isDone = true
            if (sendAlamat == "") {
                isDone = false
                vbind.etAlamat.error = getString(R.string.please_fill)
            }
            if (sendName == "") {
                isDone = false
                vbind.etName.error = getString(R.string.please_fill)
            }
            if (sendContactAmbulance == "") {
                isDone = false
                vbind.etContactAmbulance.error = getString(R.string.please_fill)
            }
            if (sendContactRS == "") {
                isDone = false
                vbind.etContactHospital.error = getString(R.string.please_fill)
            }
            if (sendOperasional == "") {
                isDone = false
                vbind.etOperasional.error = getString(R.string.please_fill)
            }
            if (sendFasilitas == "") {
                isDone = false
                vbind.etFasilitas.error = getString(R.string.please_fill)
            }
            if (sendDesc == "") {
                isDone = false
                vbind.etDeskripsi.error = getString(R.string.please_fill)
            }
            if (myLat == null) {
                isDone = false
                vbind.btnInitLocation.requestFocus()
                vbind.btnInitLocation.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.short_shake
                    )
                )
                "Lokasi Belum Dipilih".showLongToast()
            }
            if (myLong == null) {
                isDone = false
                vbind.btnInitLocation.requestFocus()
                vbind.btnInitLocation.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.short_shake
                    )
                )
                "Lokasi Belum Dipilih".showLongToast()
            }

            if (!intent.hasExtra(ID_HOSPITAL)) {
                if (imageHospital == "") {
                    vbind.ivPict.requestFocus()
                    isDone = false
                    vbind.ivPict.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.short_shake
                        )
                    )
                    "Tambah Gambar Report Terlebih Dahulu".showLongToast()
                }
            }

            if (isDone) {
                if (intent.hasExtra(ID_HOSPITAL)) {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                    "Update Hospital".showLongToast()
                    viewmodelHospital.updateHospital(
                        SendCreateHospitalModel(
                            id = id,
                            name = sendName,
                            alamat = sendAlamat,
                            operasional = sendOperasional,
                            kontak_rs = sendContactRS,
                            kontak_ambulance = sendContactAmbulance,
                            fasilitas = sendFasilitas,
                            photo = File(imageHospital?.toUri()?.path),
                            long = myLong.toString(),
                            lat = myLat.toString(),
                            deskripsi = sendDesc
                        )
                    ).observe(this, editObserver)


                } else {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                    "Create Hospital".showLongToast()
                    viewmodelHospital.createHospital(
                        SendCreateHospitalModel(
                            name = sendName,
                            alamat = sendAlamat,
                            operasional = sendOperasional,
                            kontak_rs = sendContactRS,
                            kontak_ambulance = sendContactAmbulance,
                            fasilitas = sendFasilitas,
                            photo = File(imageHospital?.toUri()?.path),
                            long = myLong.toString(),
                            lat = myLat.toString(),
                            deskripsi = sendDesc
                        )
                    ).observe(this,createObserver)
                }

            } else {
                "Periksa Inputan Terlebih Dahulu".showLongToast()
            }

        }


    }

    private fun setUpMap() {
        vbind.includeMap.let { v ->
            vbind.btnInitLocation.setOnClickListener {
                v.root.visibility = View.VISIBLE
                v.root.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_appear)
            }

            v.btnCloseDetailMap.setOnClickListener {
                v.root.visibility = View.GONE
                v.root.animation = AnimationUtils.loadAnimation(this, R.anim.bottom_gone)
            }
        }
    }

    private fun initPhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_CAMERA_REQUEST_CODE
            )
        } else {
            takePicture()
        }
    }

    private fun takePicture() {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                vbind.ivPict.setImageURI(resultUri.path?.toUri())
                imageHospital = resultUri.toString();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Camera Error", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Result Code Unknown", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }

}
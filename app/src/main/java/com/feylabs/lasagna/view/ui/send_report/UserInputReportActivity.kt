package com.feylabs.lasagna.view.ui.send_report

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.feylabs.lasagna.R
import com.feylabs.lasagna.adapter.ReportCategoryAdapter
import com.feylabs.lasagna.databinding.ActivityUserInputReportBinding
import com.feylabs.lasagna.databinding.ItemCategoryReportBinding
import com.feylabs.lasagna.data.model.api.ReportCategoryModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.util.networking.Endpoint
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.CATEGORY_ID
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.CATEGORY_IMAGE
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.CATEGORY_NAME
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.IMAGE_URI
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.LAT
import com.feylabs.lasagna.view.ui.send_report.UserReviewBeforeInputActivity.Companion.LONG
import com.feylabs.lasagna.viewmodel.CategoryViewModel
import com.feylabs.lasagna.viewmodel.InputReportViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import timber.log.Timber


class UserInputReportActivity : BaseActivity() {

    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
        private const val CODE_RESULT_URI = "res_cam_uro"
    }

    val viewModelInputReport by lazy { ViewModelProvider(this).get(InputReportViewModel::class.java) }
    val categoryViewModel by lazy { ViewModelProvider(this).get(CategoryViewModel::class.java) }
    val categoryAdapter by lazy { ReportCategoryAdapter() }

    val vbind by lazy { ActivityUserInputReportBinding.inflate(layoutInflater) }

    var isThereSelectedCategory = false
    var selectedCategoryID: Int? = null
    var reportImage = "";
    var reportCategoryImage = "";
    var reportCategoryName = "";

    var myLong: Double? = null
    var myLat: Double? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)

        Util.setStatusBarLight(this)

        requestPermission()

        setUpToolbar()
        setUpAdapter()
        setUpObserver()
        setUpRecyclerview()

        setUpMap()

        //Check Permission And Take Photo
        initPhoto()

        setUpFragment()

        categoryViewModel.getCategory()

        vbind.btnChangePhoto.setOnClickListener {
            initPhoto()
        }

        vbind.includeToolbar.myCustomToolbar.setNavigationOnClickListener {
            super.onBackPressed()
        }

    }

    private fun setUpFragment() {
        val fm = supportFragmentManager

        fm.beginTransaction()
            .add(R.id.containerMap, PickReportLocationFragment())
            .commit()
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

    private fun setUpMap() {
        vbind.includeMap.let { v ->
            vbind.btnInitLocation.setOnClickListener {
                v.root.visibility = View.VISIBLE
                v.root.animation = loadAnimation(this, R.anim.bottom_appear)
            }

            v.btnCloseDetailMap.setOnClickListener {
                v.root.visibility = View.GONE
                v.root.animation = loadAnimation(this, R.anim.bottom_gone)
            }
        }
    }

    private fun setUpRecyclerview() {
        vbind.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(applicationContext, 4)
        }
    }

    private fun setUpAdapter() {
        categoryAdapter.setInterface(object : ReportCategoryAdapter.MyCategoryInterface {
            override fun onclick(
                model: ReportCategoryModel.Data,
                adaptVbind: ItemCategoryReportBinding
            ) {
                if (model.isSelected) {
                    adaptVbind.myCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this@UserInputReportActivity,
                            R.color.colorWhite
                        )
                    )
                    model.isSelected = false
                    isThereSelectedCategory = false
                } else {
                    if (isThereSelectedCategory) {
                        vbind.rvCategory.startAnimation(
                            loadAnimation(
                                this@UserInputReportActivity,
                                R.anim.short_shake
                            )
                        )
                        "Anda Hanya Dapat Memilih 1 Category".showLongToast()
                    } else {
                        adaptVbind.myCard.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this@UserInputReportActivity,
                                R.color.colorGiok
                            )
                        )

                        val imgUrl = Endpoint.REAL_URL +model.photo_path

                        model.isSelected = true
                        isThereSelectedCategory = true

                        selectedCategoryID = model.id
                        reportCategoryImage=imgUrl
                        reportCategoryName = model.category_name

                        Preference(applicationContext).save(CATEGORY_NAME,reportCategoryName)
                        "Kategori : ${reportCategoryName}".showLongToast()
                        Timber.d("NAMA CATEGORY1 $reportCategoryName")
                    }

                }

            }
        })
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

    private fun setUpObserver() {
        categoryViewModel.categoryLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    it.data?.data?.let { categoryData ->
                        categoryAdapter.setData(categoryData)
                        categoryAdapter.notifyDataSetChanged()
                    }
                }

                is Resource.Loading -> {

                }
                is Resource.Error -> {

                }
            }
        })

        viewModelInputReport.vmLat.observe(this, Observer {
            myLat = it
        })

        viewModelInputReport.vmLong.observe(this, Observer {
            myLong = it
        })
    }


    //Check if this form completed
    private fun checkIfDone() {
        var isDone = true

        if (!isThereSelectedCategory) {
            isDone = false
            vbind.rvCategory.startAnimation(
                loadAnimation(
                    this@UserInputReportActivity,
                    R.anim.short_shake
                )
            )
            "Mohon Pilih Kategori Reporting".showLongToast()
        }

        if (reportImage == "") {
            isDone = false
            vbind.ivReportImg.startAnimation(loadAnimation(this,R.anim.short_shake))
            "Tambah Gambar Report Terlebih Dahulu".showLongToast()
        }


        if (myLat == null) {
            isDone = false
            vbind.btnInitLocation.requestFocus()
            vbind.btnInitLocation.startAnimation(loadAnimation(this, R.anim.short_shake))
            "Lokasi Belum Dipilih".showLongToast()
        }
        if (myLong == null) {
            isDone = false
            vbind.btnInitLocation.requestFocus()
            vbind.btnInitLocation.startAnimation(loadAnimation(this, R.anim.short_shake))
            "Lokasi Belum Dipilih".showLongToast()
        }

        if (isDone) {
            val intent = Intent(this, UserInputDetailActivity::class.java)

            Preference(this).save(CATEGORY_ID, selectedCategoryID!!)
            Preference(this).save(IMAGE_URI,reportImage)
            Preference(this).save(LAT,myLat.toString())
            Preference(this).save(LONG,myLong.toString())
            Preference(this).save(CATEGORY_IMAGE,reportCategoryImage)

            startActivity(intent)
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                99
            )
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
                vbind.ivReportImg.setImageURI(resultUri.path?.toUri())
                reportImage = resultUri.toString();
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
}
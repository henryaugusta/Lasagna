package com.bangkit.nadira.view.ui.report

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bangkit.nadira.R
import com.bangkit.nadira.adapter.ResponseAdapter
import com.bangkit.nadira.data.model.ReportDetailModel
import com.bangkit.nadira.databinding.ActivityDetailReportBinding
import com.bangkit.nadira.ml.ModelXceptionV2
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const
import com.bangkit.nadira.util.baseclass.BaseActivity
import com.bangkit.nadira.util.baseclass.Util
import com.bangkit.nadira.util.networking.Endpoint
import com.bangkit.nadira.view.ui.proceed.EditReportActivity
import com.bangkit.nadira.view.ui.proceed.EditReportActivity.Companion.REPORT_AIDI
import com.bangkit.nadira.viewmodel.UserReportViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import timber.log.Timber
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


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
                vbind.tvKerusakanBangunan.text = kerusakanBangunan.toString()
                vbind.tvKerusakanLain.text = kerusakanLain.toString()

                vbind.tvKorbanJiwa.text = korbanJiwa
                vbind.tvKondisiKorban.text = kondisiKorban.toString()
                vbind.tvWaktuKejadian.text = waktuKejadian
                vbind.tvPenyebab.text = peyebabKejadian

                if (model.lat?.isNotEmpty() == true && model?.long?.isNotEmpty() == true) {
                    setLocation(
                        model.lat.toDouble(),
                        model.long?.toDoubleOrNull() ?: 0.0,
                        model.detailAlamat.toString().orEmpty()
                    )
                }
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

        Glide.with(this)
            .asBitmap()
            .load(Endpoint.REAL_URL + model.photoPath)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    classifyImage(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })


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

    fun classifyImage(imagez: Bitmap) {
        try {
            val imageSize = 299
            val imageResizedz = Bitmap.createScaledBitmap(imagez, imageSize, imageSize, false)
            val model = ModelXceptionV2.newInstance(this)

            val mutableBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imageResizedz.copy(Bitmap.Config.RGBA_F16, true)
            } else {
                imageResizedz
            }

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, imageSize, imageSize, 3),
                DataType.FLOAT32
            )
            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)

            mutableBitmap.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize);

            var pixel = 0
            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val `val` = intValues[pixel++] // RGB
                    byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((`val` and 0xFF) * (1f / 255f))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)

            // Runs model inference and gets result.
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0f
            var listConf = ""
            confidences.forEachIndexed { index, fl ->
                listConf += fl.toString() + ","
            }
            Timber.d("zz confidence ${listConf.toString()}")

            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            val classes = arrayOf("Gempa Bumi", "Banjir", "Tanah Longsor", "Kebakaran")
            vbind.resultAi.text = classes[maxPos]

            // Releases model resources if no longer used.
            model.close()
        } catch (e: IOException) {
            // TODO Handle the exception
        }
    }


    private fun Uri.toBitmap(): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, this))
                .copy(Bitmap.Config.RGBA_F16, true)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, this)
        }

        return bitmap
    }


}
package com.feylabs.lasagna.view.ui.proceed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityEditReportBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.view.ui.report.DetailReportActivity
import com.feylabs.lasagna.view.ui.report.DetailReportActivity.Companion.REPORT_ID
import com.feylabs.lasagna.viewmodel.UserReportViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.*
import okhttp3.internal.wait
import timber.log.Timber
import java.io.File
import java.util.List


class EditReportActivity : BaseActivity() {

    var imageFile = ""

    override fun onStart() {
        super.onStart()
        viewModel.getAllReport()
    }

    companion object {
        const val REPORT_AIDI = "id"
    }

    val viewModel by lazy { ViewModelProvider(this).get(UserReportViewModel::class.java) }


    val aidi by lazy { intent.getStringExtra(REPORT_AIDI) }

    private val updateItem = listOf<String>(
        "Belum Diproses",
        "Sedang Diproses",
        "Dalam Koordinasi",
        "Selesai",
        "Ditolak"
    )
    val vbind by lazy { ActivityEditReportBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)

        //Inisialiasi Array Adapter dengan memasukkan String Array
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item, updateItem
        )


        //Memasukan Adapter pada Spinner
        vbind.spinner.adapter = adapter


        vbind.btnChangePhoto.setOnClickListener {
            takePicture()
        }

        vbind.btnSaveChanges.setOnClickListener {
            var error = false
            var code = vbind.spinner.selectedItemPosition
            val action = vbind.etAksi.text.toString()
            val name = vbind.etName.text.toString()
            Log.d("x_code", code.toString())
            Log.d("x_code", action.toString())
            Log.d("x_code", name.toString())
            if (imageFile == "") {
                "Pilih Foto Terlebih Dahulu".showLongToast()
                error = true
            }

            if (name == "" || name.isBlank()) {
                error = true
                "Lengkapi Nama Instansi Terlebih Dahulu".showLongToast()
            }

            if (action == "" || name.isBlank()) {
                error = true
                "Lengkapi Aksi/Tindakan Terlebih Dahulu".showLongToast()
            }

            if (!error) {
                viewModel.sendReportResponse(
                    id = aidi.toString(),
                    photo = File(imageFile),
                    responder = name,
                    status_code = code.toString(),
                    text = action
                )
            } else {
                "Periksa Inputan Terlebih Dahulu".showLongToast()
            }

        }

        setUpObserver()


    }

    private fun setUpObserver() {
        viewModel.storeResponse.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE

                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    showSweetAlert(
                        "Error",
                        it.message.toString(),
                        com.feylabs.lasagna.R.color.xdRed
                    )
                    Timber.d("myReportFragment: ->failed fetch report")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("myReportFragment: ->success fetch report")
                    showSweetAlert(
                        "Success",
                        "Berhasil Mengirim Response",
                        R.color.xdGreen
                    )
                    GlobalScope.launch {
                        delay(1500)
                        withContext(Dispatchers.Main) {
                            val intent = Intent();
                            intent.putExtra(REPORT_ID, aidi)
                            setResult(RESULT_OK, intent)
                            finish()

                        }
                    }
                }
                else -> {
                }
            }
        })


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
                Timber.d("Image Berhasil Diambil")
                imageFile = (resultUri.path.toString())
                vbind.ivPict.setImageURI(resultUri)
            }
        }
    }


}
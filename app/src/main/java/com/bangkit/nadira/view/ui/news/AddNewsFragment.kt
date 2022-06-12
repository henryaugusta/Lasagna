package com.bangkit.nadira.view.ui.news

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.FragmentAddNewsBinding
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.baseclass.BaseFragment
import com.bangkit.nadira.viewmodel.NewsViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import timber.log.Timber
import java.io.File


class AddNewsFragment : BaseFragment() {

    val newsViewModel by lazy { ViewModelProvider(requireActivity()).get(NewsViewModel::class.java) }

    lateinit var vbind: FragmentAddNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        const val CATEGORY_ID = "esdaWWve"
        const val CATEGORY_NAME = "esDdaFve"
        const val CATEGORY_PHOTO_PATH = "esdaveSD"
        private const val MY_CAMERA_REQUEST_CODE = 100
    }

    var photo = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        vbind = FragmentAddNewsBinding.bind(
            inflater.inflate(
                R.layout.fragment_add_news,
                container,
                false
            )
        )
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vbind.btnChangePhoto.setOnClickListener {
            initPhoto()
        }

        vbind.btnSaveChanges.setOnClickListener {
            var isError = false
            val s_writer = vbind.etName.text.toString()
            val s_content = vbind.etContent.text.toString()
            val s_title = vbind.etTitle.text.toString()

            if (photo == "") {
                isError = true
                "Lengkapi Foto Terlebih Dahulu".showLongToast()
            }
            if (s_writer == "") {
                isError = true
                "Lengkapi Penulis Terlebih Dahulu".showLongToast()
            }
            if (s_content == "") {
                isError = true
                "Lengkapi Konten Berita Terlebih Dahulu".showLongToast()
            }

            if (s_title == "") {
                isError = true
                "Lengkapi Judul Terlebih Dahulu".showLongToast()
            }

            if (!isError){
                newsViewModel.createNews(
                    File(photo.toUri().path),s_content,s_writer,s_title
                )
                observeNewsCreate()
            }

        }
    }

    private fun observeNewsCreate(){
        newsViewModel.createNewsResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("newsz: loading")
                    vbind.includeLoading.loadingRoot?.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbind.includeLoading?.loadingRoot?.visibility = View.GONE
                    Timber.d("newsz: error")
                    "Gagal Terhubung Dengan Server".showLongToast()
                }
                is Resource.Success -> {
                    "Success".showLongToast()
                    it.data?.let { it1 ->
                    }
                    vbind.includeLoading?.loadingRoot?.visibility = View.GONE

                }
            }
        })
    }

    private fun initPhoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
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
            .start(requireActivity(), this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                vbind.ivPict.setImageURI(resultUri.path?.toUri())
                photo = resultUri.toString()
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), "Camera Error", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Result Code Unknown", Toast.LENGTH_SHORT).show()
        }
    }


}
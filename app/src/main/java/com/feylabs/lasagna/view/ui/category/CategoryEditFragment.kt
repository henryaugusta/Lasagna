package com.feylabs.lasagna.view.ui.category

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.FragmentCategoryManageBinding
import com.feylabs.lasagna.databinding.FragmentEditCategoryBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.util.networking.Endpoint
import com.feylabs.lasagna.view.ui.contact.ContactViewModel
import com.feylabs.lasagna.view.ui.contact.CreateContactFragment
import com.feylabs.lasagna.view.ui.hospital.AddHospitalActivity
import com.feylabs.lasagna.viewmodel.CategoryViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File


class CategoryEditFragment() : BaseFragment() {


    lateinit var vbind: FragmentEditCategoryBinding
    lateinit var viewmodel: CategoryViewModel

    private val cat_id by lazy { requireArguments().getString(CATEGORY_ID).toString() }
    private val cat_name by lazy { requireArguments().getString(CATEGORY_NAME).toString() }
    private val cat_photo by lazy { requireArguments().getString(CATEGORY_PHOTO_PATH).toString() }

    var photo = ""

    companion object {
        const val CATEGORY_ID = "esdaWWve"
        const val CATEGORY_NAME = "esDdaFve"
        const val CATEGORY_PHOTO_PATH = "esdaveSD"
        private const val MY_CAMERA_REQUEST_CODE = 100
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        vbind = FragmentEditCategoryBinding.bind(
            inflater.inflate(
                R.layout.fragment_edit_category,
                container,
                false
            )
        )
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(requireActivity()).get(CategoryViewModel::class.java)


        if (cat_id != "add") {
            Glide
                .with(vbind.root)
                .load(Endpoint.REAL_URL + cat_photo)
                .skipMemoryCache(true)
                .dontAnimate()
                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                .placeholder(R.drawable.ic_loading_small_1)
                .into(vbind.ivPict)

            vbind.etName.setText(cat_name)
        }

        vbind.btnChangePhoto.setOnClickListener {
            initPhoto()
        }

        vbind.btnSaveChanges.setOnClickListener {
            var error = false
            val title = vbind.etName.text.toString()
            if (title.isNullOrEmpty()) {
                error = true
                "Mohon Melengkapi Input Terlebih Dahulu".showLongToast()
            }

            if (cat_id == "add") {
                if (photo == "") {
                    error = true
                    "Mohon Melengkapi Foto Terlebih Dahulu".showLongToast()
                }
            }

            // iF NOT ERROR OR INPUT IS OK
            if (!error) {
                var sendedPhoto = File(photo?.toUri()?.path)
                if (cat_id == "add") {
                    // If in add model
                    observeStore()
                    viewmodel.storeCategory(title, sendedPhoto)
                } else {
                    // if in edit model
                    if (photo==""){
                        viewmodel.updateCategory(title, null,cat_id)
                    }else{
                        viewmodel.updateCategory(title, sendedPhoto,cat_id)
                    }
                    observeEdit()

                }
            }

        }


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

    private fun observeStore() {
        viewmodel.storeCategoryLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    vbind.includeLoadingFull.loadingRoot.setGone()
                    showSweetAlert(
                        "Success",
                        "Berhasil Menambah Kategori",
                        R.color.xdGreen
                    )
                    viewmodel.storeCategoryLiveData.value=null
                    "Berhasil Menambah Kategori".showLongToast()
                }
                is Resource.Loading -> {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert(
                        "Error",
                        "Gagal Menambah Kategori",
                        R.color.xdRed
                    )
                    viewmodel.storeCategoryLiveData.value=null
                    vbind.includeLoadingFull.loadingRoot.setGone()
                }
                else -> {
                }
            }
        })
    }

    private fun observeEdit() {
        viewmodel.updateCategoryLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    vbind.includeLoadingFull.loadingRoot.setGone()
                    showSweetAlert(
                        "Success",
                        "Berhasil Mengupdate Kategori",
                        R.color.xdGreen
                    )
                    viewmodel.updateCategoryLiveData.value=null
                    "Berhasil Mengupdate Kategori".showLongToast()
                }
                is Resource.Loading -> {
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert(
                        "Error",
                        "Gagal Mengupdate Kategori",
                        R.color.xdRed
                    )
                    viewmodel.updateCategoryLiveData.value=null
                    vbind.includeLoadingFull.loadingRoot.setGone()
                }
                else -> {
                }
            }
        })
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
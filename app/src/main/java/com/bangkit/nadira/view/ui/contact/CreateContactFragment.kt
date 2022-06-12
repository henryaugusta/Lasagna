package com.bangkit.nadira.view.ui.contact

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.R
import com.bangkit.nadira.data.LasagnaRepository
import com.bangkit.nadira.data.remote.RemoteDataSource
import com.bangkit.nadira.databinding.FragmentCreateContactBinding
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.baseclass.BaseFragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File


class CreateContactFragment : BaseFragment() {

    companion object {
        private const val MY_CAMERA_REQUEST_CODE = 100
        const val ID_HOSPITAL = "id_hospital"
    }

    var imageFile = ""
    lateinit var vbind: FragmentCreateContactBinding

    lateinit var viewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vbind = FragmentCreateContactBinding.bind(
            inflater.inflate(
                R.layout.fragment_create_contact,
                container,
                false
            )
        )
        // Inflate the layout for this fragment
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tambah Instansi"

        vbind.btnChangePhoto.setOnClickListener {
            initPhoto()
        }

        val factory = ContactViewModelFactory(LasagnaRepository(RemoteDataSource()))
        viewModel = ViewModelProvider(requireActivity(), factory).get(ContactViewModel::class.java)

        val createObserver = Observer<Resource<String>> {
            when (it) {
                is Resource.Success -> {
                    Log.d("state contact","success")
                    showSweetAlert(
                        "Success",
                        "Berhasil Menambah Kontak Instansi",
                        R.color.xdGreen
                    )
                    vbind.includeLoadingFull.loadingRoot.setGone()

                }
                is Resource.Loading -> {
                    Log.d("state contact","loading")
                    vbind.includeLoadingFull.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    Log.d("state contact","error")
                    showSweetAlert(
                        "Error",
                        "Gagal Menginput Kontak Instansi",
                        R.color.xdRed
                    )
                    vbind.includeLoadingFull.loadingRoot.setGone()
                }
                else -> {
                }
            }
        }


        vbind.btnSaveChanges.setOnClickListener {
            val title = vbind.etName.text.toString()
            val number = vbind.etContact.text.toString()
            var isError = false
            if (imageFile == "") {
                isError = true
                "Pilih Foto Sebelum Melanjutkan".showLongToast()
            }
            if (title == "") {
                isError = true
                "Masukkan Nama Instansi".showLongToast()
            }
            if (number == "") {
                isError = true
                "Masukkan Nama Instansi".showLongToast()
            }

            if (!isError) {
                viewModel.storeContact(title, number, File(imageFile.toUri().path.toString()))
                    .observe(
                        viewLifecycleOwner, createObserver
                    )
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri: Uri = result.uri
                vbind.ivPict.setImageURI(resultUri.path?.toUri())
                imageFile = resultUri.toString()
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), "Camera Error", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Result Code Unknown", Toast.LENGTH_SHORT).show()
        }
    }


}
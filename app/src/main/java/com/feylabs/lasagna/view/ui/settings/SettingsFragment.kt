package com.feylabs.lasagna.view.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.SettingsFragmentBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.viewmodel.ProfileViewModel
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.UserPreferenceHelper
import com.feylabs.lasagna.util.SharedPreference.const.USER_EMAIL
import com.feylabs.lasagna.util.SharedPreference.const.USER_ID
import com.feylabs.lasagna.util.SharedPreference.const.USER_NAME
import com.feylabs.lasagna.util.SharedPreference.const.USER_PHOTO
import com.feylabs.lasagna.util.SharedPreference.const.USER_USERNAME
import com.feylabs.lasagna.util.networking.Endpoint.REAL_URL
import com.feylabs.lasagna.view.MainMenuUserViewModel
import com.feylabs.lasagna.view.take_photo_utility.UserChangePhotoActivity
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage

import timber.log.Timber

class SettingsFragment : BaseFragment() {


    lateinit var vbind: SettingsFragmentBinding

    val pref by lazy { Preference(requireContext()) }

    val profileViewModel by lazy { ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java) }
    val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }

    override fun onStart() {
        super.onStart()
        profileViewModel.retrieveProfile(
            pref.getPrefString(USER_ID).toString()
        )
        updateLayoutFromPreference()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)
        vbind = SettingsFragmentBinding.bind(view)
        return vbind.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profileViewModel.retrieveProfile(
            pref.getPrefString(USER_ID).toString()
        )
        updateLayoutFromPreference()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuViewModel.title.value = "Profile"

        vbind.btnChangePhoto.setOnClickListener {
            startActivity(Intent(requireActivity(),UserChangePhotoActivity::class.java))
        }

        vbind.btnSavePassword.setOnClickListener {
            var error = false;
            vbind.apply {
                if (etOldPassword.text.toString().isBlank()) {
                    error = true
                }
                if (etNewPassword.text.toString().isBlank()) {
                    error = true
                }
                if (etNewPassword.text.toString().length < 6) {
                    error = true
                    etNewPassword.error = "Password Baru Harus lebih dari 6 karakter"
                }
                if (error) {
                    "Mohon Lengkapi Semua Form".showLongToast()
                } else {
                    val id = Preference(requireContext()).getPrefString(USER_ID).toString()
                    profileViewModel.updatePassword(
                        id,
                        old_pass = etOldPassword.text.toString(),
                        new_pass = etNewPassword.text.toString()
                    )
                }
            }
        }

        //On User Click Save "Simpan Perubahan/Save Profile"
        vbind.btnSaveChange.setOnClickListener {
            var error = false;
            vbind.apply {
                if (etName.text.toString().isBlank()) {
                    error = true
                }
                if (etUsername.text.toString().isBlank()) {
                    error = true
                }
                if (etContact.text.toString().isBlank()) {
                    error = true
                }
                if (etEmail.text.toString().isBlank()) {
                    error = true
                }

                if (error) {
                    "Lengkapi Profile Terlebih Dahulu".showLongToast()
                } else {
                    "Mengupdate Profile".showLongToast()
                    profileViewModel.updateProfile(
                        id = Preference(requireContext()).getPrefString(USER_ID).toString(),
                        username = etUsername.text.toString(),
                        name = etName.text.toString(),
                        email = etEmail.text.toString(),
                        kontak = etContact.text.toString()
                    )
                }
            }
        }

        profileViewModel.updatePassword.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("profileFragment: ->changePass loading")
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE

                    if (it.data == 0)
                        showSweetAlert("Error", "Gagal Mengganti Password", R.color.xdRed)
                    if (it.data == 3)
                        showSweetAlert("Error", "Password Lama Tidak Sesuai", R.color.xdRed)

                    Timber.d("profileFragment: ->changePass error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    //If Success, Retrieve Profile Again
                    showSweetAlert("Success", "Berhasil Mengupdate Password", R.color.xdGreen)

                    profileViewModel.retrieveProfile(
                        Preference(requireContext()).getPrefString(USER_ID).toString()
                    )

                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("profileFragment: ->update success")
                }
            }
        })

        profileViewModel.updateStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("profileFragment: ->update loading")
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    showSweetAlert("Error", "Gagal Mengupdate Akun", R.color.xdRed)

                    Timber.d("profileFragment: ->update error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    //If Success, Retrieve Profile Again
                    showSweetAlert("Success", "Berhasil Mengupdate Akun", R.color.xdGreen)


                    profileViewModel.retrieveProfile(
                        Preference(requireContext()).getPrefString(USER_ID).toString()
                    )

                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("profileFragment: ->update success")
                }
            }
        })

        profileViewModel.updatePhoto.observe(requireActivity(), Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("profileFragment: ->changePass loading")
                }
                is Resource.Error -> {
                    Timber.d("profileFragment: ->changePass error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    //If Success, Retrieve Profile Again
                    showSweetAlert("Success", "Berhasil Mengupdate Password", R.color.xdGreen)
                    Timber.d("profileFragment: ->update success")
                }
            }
        })

        profileViewModel.peopleModel.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("profileFragment: loading")
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("profileFragment: error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {

                    val username = it.data?.username.toString()
                    val email = it.data?.email.toString()
                    val contact = it.data?.contact.toString()
                    val name = it.data?.nama.toString()
                    val nik = it.data?.nik.toString()
                    val photo_path = REAL_URL+it.data?.photo_path.toString()
                    val gender = it.data?.jk.toString()

                    vbind.etContact.setText(contact)
                    vbind.etName.setText(name)
                    vbind.etEmail.setText(email)
                    vbind.etUsername.setText(username)
                    vbind.includeLoading.loadingRoot.visibility = View.GONE

                    UserPreferenceHelper.updateUserPreference(
                        requireContext(),
                        name = name,
                        username = username,
                        email = email,
                        contact = contact,
                        photo = photo_path,
                        gender = gender,
                        nik = nik
                    )

                    updateLayoutFromPreference()


                    Timber.d("profileFragment: success")
                }
            }
        })
    }

    private fun takePicture() {
        CropImage.activity()
            .start(requireContext(), this);
    }


    private fun updateLayoutFromPreference() {

        val pref = Preference(requireContext())

        pref.let {

            vbind.etName.setText(it.getPrefString(USER_NAME).toString())
            vbind.etEmail.setText(it.getPrefString(USER_EMAIL).toString())

            vbind.etUsername.setText(it.getPrefString(USER_USERNAME).toString())

            Timber.d("photo_url : ${it.getPrefString(USER_PHOTO)}")

            if (it.getPrefString(USER_PHOTO).toString().isNotEmpty()) {
                Picasso
                    .get()
                    .load(it.getPrefString(USER_PHOTO))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_empty_profile)
                    .into(vbind.ivProfilePict)
            }

        }


        vbind.tvName.text = Preference(requireContext()).getPrefString(USER_NAME).toString()
    }


    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ("Accept All Permission Request").showLongToast()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

}
package com.feylabs.lasagna.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.feylabs.lasagna.model.ModelNewsCarousel
import com.feylabs.lasagna.model.PeopleModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.networking.Endpoint
import org.json.JSONObject
import timber.log.Timber
import java.io.File

class ProfileViewModel : ViewModel() {

    val peopleModel: MutableLiveData<Resource<PeopleModel>> = MutableLiveData()
    val updateStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val updatePassword: MutableLiveData<Resource<Int>> = MutableLiveData()

    val updatePhoto: MutableLiveData<Resource<String>> = MutableLiveData()

    fun retrieveProfile(id: String) {
        peopleModel.postValue(Resource.Loading())
        Timber.d("user_id : $id")
        AndroidNetworking.post(Endpoint.PEOPLE_DETAIL(id))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Timber.d("response: success -> ${response?.toString()}")
                    if (response?.getInt("status") == 1) {
                        parsePeopleJSON(response.getJSONObject("user"))
                    } else {
                        peopleModel.postValue(Resource.Error("Error"))
                    }
                }

                override fun onError(anError: ANError?) {
                    peopleModel.postValue(Resource.Error("Error"))
                    Timber.d("profile: error -> ${anError?.message}")
                    Timber.d("profile: error -> ${anError?.errorCode}")
                    Timber.d("profile: error -> ${anError?.errorDetail}")
                    Timber.d("profile: error -> ${anError?.errorBody}")
                }

            })
    }

    fun updateProfile(
        id: String,
        name: String,
        kontak: String,
        email: String,
        username: String
    ) {
        peopleModel.postValue(Resource.Loading())
        Timber.d("user_id : $id")
        AndroidNetworking.post(Endpoint.PEOPLE_DETAIL_UPDATE(id))
            .addBodyParameter("nama", name)
            .addBodyParameter("username", username)
            .addBodyParameter("email", email)
            .addBodyParameter("no_telp", kontak)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Timber.d("response: success -> ${response?.toString()}")
                    if (response?.getInt("status") == 1) {
                        updateStatus.postValue(Resource.Success("Berhasil Mengupdate Data"))
                    } else {
                        updateStatus.postValue(Resource.Error("Gagal Mengupdate Data"))
                    }
                }

                override fun onError(anError: ANError?) {
                    updateStatus.postValue(Resource.Error("Gagal Mengupdate Data"))
                    Timber.d("profile: error -> ${anError?.message}")
                    Timber.d("profile: error -> ${anError?.errorCode}")
                    Timber.d("profile: error -> ${anError?.errorDetail}")
                    Timber.d("profile: error -> ${anError?.errorBody}")
                }

            })
    }


    fun updatePassword(
        id: String,
        old_pass: String,
        new_pass: String
    ) {
        peopleModel.postValue(Resource.Loading())
        Timber.d("user_id : $id")
        AndroidNetworking.post(Endpoint.PEOPLE_UPDATE_PASSWORD(id))
            .addBodyParameter("old_password", old_pass)
            .addBodyParameter("new_password", new_pass)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Timber.d("response pass: success -> ${response?.toString()}")
                    if (response?.getInt("status") == 1) {
                        updatePassword.postValue(Resource.Success(1))
                    }
                    if (response?.getInt("status") == 0) {
                        updatePassword.postValue(Resource.Error("Gagal Mengupdate Password", 0))
                    }
                    if (response?.getInt("status") == 3) {
                        updatePassword.postValue(Resource.Error("Password Lama Tidak Sesuai", 3))
                    }
                }

                override fun onError(anError: ANError?) {
                    updatePassword.postValue(
                        Resource.Error(
                            "Gagal Mengupdate Password , Coba Lagi Nanti",
                            0
                        )
                    )
                    Timber.d("profile pass: error -> ${anError?.message}")
                    Timber.d("profile pass: error -> ${anError?.errorCode}")
                    Timber.d("profile pass: error -> ${anError?.errorDetail}")
                    Timber.d("profile pass: error -> ${anError?.errorBody}")
                }

            })
    }

    fun updateImage(id:String,photo: File){
        updatePhoto.value=Resource.Loading()
        AndroidNetworking.upload(Endpoint.PEOPLE_UPDATE_IMAGE(id))
            .addMultipartFile("photo",photo)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    Timber.d("response update_profile: success -> ${response?.toString()}")
                    if (response?.getInt("status_code")==1){
                        updatePhoto.value=Resource.Success("Berhasil Mengupdate Foto Profile")
                    }else{
                        updatePhoto.value=Resource.Error("Error saat Mengupdate Foto Profile")
                    }
                }

                override fun onError(anError: ANError?) {
                    updatePhoto.value=Resource.Error("Gagal Mengupdate Foto Profile")
                    Timber.d("profile_photo : error -> ${anError?.message}")
                    Timber.d("profile_photo : error -> ${anError?.errorCode}")
                    Timber.d("profile_photo : error -> ${anError?.errorDetail}")
                    Timber.d("profile_photo : error -> ${anError?.errorBody}")
                }

            })

    }

    private fun parsePeopleJSON(jsonObject: JSONObject) {
        jsonObject.let {
            val peopleFromDB = PeopleModel(
                id = it.getString("id"),
                nama = it.getString("nama"),
                nik = it.getString("nik"),
                username = it.getString("username"),
                email = it.getString("email"),
                contact = it.getString("no_telp"),
                jk = it.getString("jk"),
                created_at = it.getString("created_at"),
                updated_at = it.getString("updated_at"),
                photo_path = it.getString("photo_path")
            )

            peopleModel.postValue(
                Resource.Success(peopleFromDB)
            )

        }
    }

}
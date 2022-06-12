package com.bangkit.nadira.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.networking.Endpoint
import org.json.JSONObject
import timber.log.Timber

class RegisterViewModel : ViewModel() {

    val registerStatus: MutableLiveData<Resource<String>> = MutableLiveData()

    fun sendRegisterData(
        nama: String,
        username: String,
        email: String,
        password: String,
        jk: String,
        no_telp: String
    ) {

        registerStatus.postValue(Resource.Loading())

        Timber.d("register: argument-> 1$nama - 2$username - 3$email - 4$password - 5$jk - 6$no_telp")
        AndroidNetworking.post(Endpoint.PEOPLE_REGISTER)
            .addBodyParameter("nama", nama)
            .addBodyParameter("username", username)
            .addBodyParameter("email", email)
            .addBodyParameter("password", password)
            .addBodyParameter("jk", jk)
            .addBodyParameter("no_telp", no_telp)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (response.getString("status")=="1"){
                        registerStatus.postValue(Resource.Success("Registrasi Berhasil"))
                    }else{
                        registerStatus.postValue(Resource.Error("Registrasi Gagal"))
                    }
                }

                override fun onError(anError: ANError?) {
                    Timber.d("register: error -> ${anError?.message}")
                    Timber.d("register: error -> ${anError?.errorCode}")
                    Timber.d("register: error -> ${anError?.errorDetail}")
                    Timber.d("register: error -> ${anError?.errorBody}")
                    registerStatus.postValue(Resource.Error(anError?.errorBody.toString()))
                }

            })
    }


}
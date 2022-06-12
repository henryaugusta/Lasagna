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

class LoginViewModel : ViewModel() {

    val loginStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val USER_NAME : MutableLiveData<String> = MutableLiveData()
    val USER_USERNAME : MutableLiveData<String> = MutableLiveData()
    val USER_ID : MutableLiveData<String> = MutableLiveData()
    val USER_EMAIL : MutableLiveData<String> = MutableLiveData()
    val USER_TYPE : MutableLiveData<String> = MutableLiveData()

    fun sendLoginData(
        username: String,
        password: String
    ) {

        loginStatus.postValue(Resource.Loading())

        Timber.d("login: argument-> $username,$password")
        AndroidNetworking.post(Endpoint.PEOPLE_LOGIN)
            .addBodyParameter("username", username)
            .addBodyParameter("password", password)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Timber.d("login: response-> $response")
                    if (response.getString("status")=="1"){
                        if (response.getString("type")=="admin"){
                            val people = response.getJSONObject("people")
                            USER_TYPE.postValue("admin")
                            USER_ID.postValue(people.getString("id"))
                            USER_NAME.postValue(people.getString("name"))
                            USER_EMAIL.postValue(people.getString("email"))
                            loginStatus.postValue(Resource.Success("Login Berhasil (Admin)"))
                        }else{
                            val people = response.getJSONObject("people")
                            //these are 4 main pref for user people
                            USER_ID.postValue(people.getString("id"))
                            USER_NAME.postValue(people.getString("nama"))
                            USER_USERNAME.postValue(people.getString("username"))
                            USER_EMAIL.postValue(people.getString("email"))
                            loginStatus.postValue(Resource.Success("Login Berhasil"))
                        }

                    }else{
                        loginStatus.postValue(Resource.Error("Username atau Password Tidak Ditemukan"))
                    }
                }

                override fun onError(anError: ANError?) {
                    Timber.d("register: error -> ${anError?.message}")
                    Timber.d("register: error -> ${anError?.errorCode}")
                    Timber.d("register: error -> ${anError?.errorDetail}")
                    Timber.d("register: error -> ${anError?.errorBody}")
                    loginStatus.postValue(Resource.Error(anError?.errorBody.toString()))
                }

            })
    }


}
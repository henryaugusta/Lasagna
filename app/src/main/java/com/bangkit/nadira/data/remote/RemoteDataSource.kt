package com.bangkit.nadira.data.remote

import android.annotation.SuppressLint
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.bangkit.nadira.data.model.SendCreateHospitalModel
import com.bangkit.nadira.data.model.api.*
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.URL.INDONESIA_SUMMARY
import com.bangkit.nadira.util.networking.Endpoint
import com.bangkit.nadira.util.networking.Endpoint.CREATE_CONTACT
import com.bangkit.nadira.util.networking.Endpoint.DELETE_CONTACT
import com.bangkit.nadira.util.networking.Endpoint.GET_CONTACT
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File

@SuppressLint("LogNotTimber")
class RemoteDataSource {

    var gson: Gson = Gson()

    fun getHospital(callback: GetHospitalCallback) {
        callback.callback(Resource.Loading())
        gson = Gson()
        AndroidNetworking.get(Endpoint.HOSPITAL_FETCH)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("sdazzz", response.toString())
                    val res = gson.fromJson(response, HospitalModel::class.java)
                    if (res.status == 1) {
                        callback.callback(Resource.Success(res.data))
                    } else {
                        callback.callback(Resource.Error("Something Wrong"))
                    }
                }

                override fun onError(anError: ANError?) {
                    callback.callback(Resource.Error(anError?.localizedMessage.toString()))
                }

            })
    }

    fun getHospital(id: String, callback: DetailHospitalCallback) {
        callback.callback(Resource.Loading())
        gson = Gson()
        AndroidNetworking.get(Endpoint.HOSPITAL_DETAIL(id))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("sdazzz", response.toString())
                    if (response.getInt("status") == 1) {
                        val hospital = gson.fromJson(
                            response.getJSONObject("data").toString(),
                            HospitalModel.Data::class.java
                        )
                        callback.callback(Resource.Success(hospital))
                    } else {
                        callback.callback(Resource.Error(response.getString("message")))
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("sdazzz", anError?.errorBody.toString())
                    callback.callback(Resource.Error(anError?.errorBody.toString()))
                }

            })
    }


    fun deleteHospital(id: String, callback: DeleteHospitalCallback) {
        callback.callback(Resource.Loading())
        gson = Gson()
        AndroidNetworking.delete(Endpoint.HOSPITAL_DELETE(id))
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("sdazzz", response.toString())
                    val res = gson.fromJson(response, DeleteHospitalModel::class.java)
                    if (res.status == 1) {
                        callback.callback(Resource.Success(res, res.message))
                    } else {
                        callback.callback(Resource.Error("Something Wrong"))
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("sdazzz", anError?.errorBody.toString())
                    callback.callback(Resource.Error(anError?.errorBody.toString()))
                }

            })
    }

    fun addHospital(model: SendCreateHospitalModel, callback: AddHospitalCallback) {
        gson = Gson()
        val request = AndroidNetworking.upload(
            Endpoint.HOSPITAL_SEND()
        )

        request.apply {
            if (model.photo != null) {
                Log.d("photo_hsp", model.photo.toString())
                addMultipartFile("photo", model.photo)
            }
            addMultipartParameter("name", model.name)
            addMultipartParameter("alamat", model.alamat)
            addMultipartParameter("deskripsi", model.deskripsi)
            addMultipartParameter("long", model.long)
            addMultipartParameter("lat", model.lat)
            addMultipartParameter("fasilitas", model.fasilitas)
            addMultipartParameter("kontak_ambulance", model.kontak_ambulance)
            addMultipartParameter("kontak_rs", model.kontak_rs)
            addMultipartParameter("operasional", model.operasional)
            build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        Log.d("sdaxxx", response.toString())
                        if (response?.getInt("status") == 1) {
                            callback.callback(Resource.Success("Success"))
                        } else {
                            callback.callback(Resource.Success("Failed"))
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Log.d("sdaxxx", anError?.errorBody.toString())
                        callback.callback(Resource.Error(anError?.errorBody.toString()))
                    }

                })
        }

    }

    fun editHospital(model: SendCreateHospitalModel, callback: AddHospitalCallback) {
        Log.d("photo_hsp", "photo ${model.photo.toURI().path}")
        val request = AndroidNetworking.upload(Endpoint.HOSPITAL_UPDATE(model.id))
        request.apply {
            if (model.photo.toString() != "") {
                addMultipartFile("photo", model.photo)
            }
            addMultipartParameter("name", model.name)
            addMultipartParameter("alamat", model.alamat)
            addMultipartParameter("deskripsi", model.deskripsi)
            addMultipartParameter("long", model.long)
            addMultipartParameter("lat", model.lat)
            addMultipartParameter("fasilitas", model.fasilitas)
            addMultipartParameter("kontak_ambulance", model.kontak_ambulance)
            addMultipartParameter("kontak_rs", model.kontak_rs)
            addMultipartParameter("operasional", model.operasional)
            build().getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d("rep_edit_hospital", "success")
                    Log.d("rep_edit_hospital", response.toString())
                    if (response?.getInt("status") == 1) {
                        callback.callback(Resource.Success("Success"))
                    } else {
                        callback.callback(Resource.Success("Failed"))
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_edit_hospital", "failed")
                    Log.d("rep_edit_hospital", anError?.errorCode.toString())
                    Log.d("rep_edit_hospital", anError?.message.toString())
                    callback.callback(Resource.Error(anError?.errorBody.toString()))
                }

            })
        }


    }

    fun getWeather(id: Int = 501369, callback: DetailWeatherCallback) {
        AndroidNetworking.get(Endpoint.GET_WEATHER(id.toString())).build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("rep_detail_weather", "success")
                    Log.d("rep_detail_weather", response.toString())
                    val gson = Gson()
                    val model = gson.fromJson(response, Weather::class.java)
                    callback.callback(
                        Resource.Success(
                            model,
                            "Berhasil Menampilkan Prediksi Cuaca"
                        )
                    )
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_detail_weather", "error")
                    Log.d("rep_detail_weather", anError?.errorBody.toString())
                    Log.d("rep_detail_weather", anError?.message.toString())
                    callback.callback(Resource.Error("Terjadi Kesalahan"))
                }

            })
    }

    fun getCityWeather(callback: ListCityWeatherCallback) {
        AndroidNetworking.get(Endpoint.GET_CITY_WEATHER()).build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("rep_detail_weather", "success")
                    Log.d("rep_detail_weather", response.toString())
                    val gson = Gson()
                    val model = gson.fromJson(response, CityWeather::class.java)
                    callback.callback(Resource.Success(model, "Berhasil Fetch Data Kota"))
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_detail_weather", "error")
                    Log.d("rep_detail_weather", anError?.errorBody.toString())
                    Log.d("rep_detail_weather", anError?.message.toString())
                    callback.callback(Resource.Error("Terjadi Kesalahan"))
                }

            })
    }

    fun getContact(callback: GetContactCallback) {
        callback.callback(Resource.Loading())
        AndroidNetworking.get(GET_CONTACT).build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("rep_contact", "success")
                    Log.d("rep_contact", response.toString())
                    val model = Gson().fromJson(response, ContactResponseModel::class.java)
                    callback.callback(Resource.Success(model, model.message))
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_contact", "error")
                    Log.d("rep_contact", anError?.errorBody.toString())
                    Log.d("rep_contact", anError?.message.toString())
                    callback.callback(Resource.Error("Terjadi Kesalahan"))
                }

            })
    }

    fun getDetailContact(){

    }

    fun createContact(
        name: String,
        description: String,
        photo: File,
        callback: AddContactCallback
    ) {
        callback.callback(Resource.Loading())
        AndroidNetworking.upload(CREATE_CONTACT)
            .addMultipartParameter("name", name)
            .addMultipartParameter("deskripsi", description)
            .addMultipartFile("photo", photo)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("rep_contact_add", "success")
                    Log.d("rep_contact_add", response.toString())
                    val model = Gson().fromJson(response, ContactResponseModel::class.java)
                    callback.callback(Resource.Success(model.message,model.message))
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_contact_add", "error")
                    Log.d("rep_contact_add", anError?.errorBody.toString())
                    Log.d("rep_contact_add", anError?.message.toString())
                    callback.callback(Resource.Error("Terjadi Kesalahan"))
                }

            })
    }

    fun deleteContact(
        id: String,
        callback: DeleteContactCallback
    ) {
        callback.callback(Resource.Loading())
        AndroidNetworking.delete(DELETE_CONTACT(id))
            .addBodyParameter("id",id)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("rep_contact_add", "success")
                    Log.d("rep_contact_add", response.toString())
                    val model = Gson().fromJson(response, ContactDeleteResponse::class.java)
                    if (model.status==1){
                        callback.callback(Resource.Success(model.message,model.message))
                    }else{
                        if (model.status==1){
                            callback.callback(Resource.Error(model.message))
                        }
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_contact_delete", "error")
                    Log.d("rep_contact_delete", anError?.errorBody.toString())
                    Log.d("rep_contact_delete", anError?.message.toString())
                    callback.callback(Resource.Error("Terjadi Kesalahan"))
                }

            })
    }

    fun covidIDSummary(callback: CovidSummaryCallback) {
        AndroidNetworking.get(INDONESIA_SUMMARY).build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Log.d("rep_kopit", "success")
                    Log.d("rep_kopit", response.toString())
                    val model = gson.fromJson(response, GovCovidData::class.java)
                    if (model != null) {
                        callback.callback(Resource.Success(model, "Berhasil Memuat Data Covid"))
                    }
                }

                override fun onError(anError: ANError?) {
                    Log.d("rep_kopit", "error")
                    Log.d("rep_kopit", anError?.errorBody.toString())
                    Log.d("rep_kopit", anError?.message.toString())
                    callback.callback(Resource.Error("Gagal Memuat Data Covid"))
                }

            })
    }

    interface GetHospitalCallback {
        fun callback(response: Resource<List<HospitalModel.Data>>)
    }

    interface DeleteHospitalCallback {
        fun callback(response: Resource<DeleteHospitalModel>)
    }

    interface AddHospitalCallback {
        fun callback(response: Resource<String>)
    }

    interface DetailHospitalCallback {
        fun callback(response: Resource<HospitalModel.Data>)
    }

    interface DetailWeatherCallback {
        fun callback(response: Resource<Weather>)
    }

    interface ListCityWeatherCallback {
        fun callback(response: Resource<CityWeather>)
    }

    interface CovidSummaryCallback {
        fun callback(response: Resource<GovCovidData>)
    }

    interface GetContactCallback {
        fun callback(response: Resource<ContactResponseModel>)
    }

    interface AddContactCallback {
        fun callback(response: Resource<String>)
    }

    interface DeleteContactCallback {
        fun callback(response: Resource<String>)
    }
}


package com.feylabs.lasagna.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.feylabs.lasagna.data.model.api.ReportCategoryModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.networking.Endpoint
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber
import java.io.File


class CategoryViewModel : ViewModel() {

    val categoryLiveData: MutableLiveData<Resource<ReportCategoryModel>> = MutableLiveData()
    val deleteCategoryLiveData: MutableLiveData<Resource<String>> = MutableLiveData()
    val storeCategoryLiveData: MutableLiveData<Resource<String>> = MutableLiveData()
    val updateCategoryLiveData: MutableLiveData<Resource<String>> = MutableLiveData()

    fun getCategory() {
        val gson = Gson()
        categoryLiveData.value = Resource.Loading()
        AndroidNetworking.get(Endpoint.GET_REPORT_CATEGORY)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    val myobject: ReportCategoryModel =
                        gson.fromJson(response.toString(), ReportCategoryModel::class.java)
                    Timber.d("category: ${myobject.size} ")
                    Timber.d("category: ${myobject.http_response} ")
                    Timber.d("category: ${myobject.message} ")
                    categoryLiveData.postValue(Resource.Success(myobject))

                }

                override fun onError(anError: ANError?) {
                    Timber.d("category: error -> ${anError?.message}")
                    Timber.d("category: error -> ${anError?.errorCode}")
                    Timber.d("category: error -> ${anError?.errorDetail}")
                    Timber.d("category: error -> ${anError?.errorBody}")
                }
            })
    }

    fun deleteCategory(id: String) {
        deleteCategoryLiveData.value = Resource.Loading()
        AndroidNetworking.get(Endpoint.DELETE_CATEGORY(id))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    if (response?.getInt("http_response") == 200) {
                        deleteCategoryLiveData.value =
                            Resource.Success("", "Berhasil Menghapus Data")
                    } else {
                        deleteCategoryLiveData.value = Resource.Error("Gagal Menghapus Data")
                    }
                }

                override fun onError(anError: ANError?) {
                    deleteCategoryLiveData.value =
                        Resource.Error("Gagal Menghapus Data (Koneksi Error)")
                    Timber.d("category: error -> ${anError?.message}")
                    Timber.d("category: error -> ${anError?.errorCode}")
                    Timber.d("category: error -> ${anError?.errorDetail}")
                    Timber.d("category: error -> ${anError?.errorBody}")
                }

            })
    }

    fun storeCategory(name: String, file: File?) {
        storeCategoryLiveData.value = Resource.Loading()
        val send = AndroidNetworking.upload(Endpoint.CREATE_CATEGORY)
        if (file != null) {
            send.addMultipartFile("icon", file)
        }
        send.addMultipartParameter("title", name)
        send.build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response?.getInt("http_response") == 200) {
                        storeCategoryLiveData.value =
                            Resource.Success("Berhasil Menambahkan Data")
                    } else {
                        storeCategoryLiveData.value = Resource.Error("Gagal Menambahkan Data")
                    }
                }

                override fun onError(anError: ANError?) {
                    storeCategoryLiveData.value =
                        Resource.Error("Gagal Menambahkan Data (Koneksi Error)")
                    Timber.d("category add: error -> ${anError?.message}")
                    Timber.d("category add: error -> ${anError?.errorCode}")
                    Timber.d("category add: error -> ${anError?.errorDetail}")
                    Timber.d("category add: error -> ${anError?.errorBody}")
                }
            })
    }

    fun updateCategory(name: String, file: File? = null,id:String) {
        updateCategoryLiveData.value = Resource.Loading()
        val send = AndroidNetworking.upload(Endpoint.EDIT_CATEGORY(id))
        if (file != null) {
            send.addMultipartFile("icon", file)
        }
        send.addMultipartParameter("title", name)
        send.build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response?.getInt("http_response") == 200) {
                        updateCategoryLiveData.value =
                            Resource.Success("Berhasil Mengupdate Data")
                    } else {
                        updateCategoryLiveData.value = Resource.Error("Gagal Mengupdate Data")
                    }
                }

                override fun onError(anError: ANError?) {
                    updateCategoryLiveData.value =
                        Resource.Error("Gagal Menambahkan Data (Koneksi Error)")
                    Timber.d("category add: error -> ${anError?.message}")
                    Timber.d("category add: error -> ${anError?.errorCode}")
                    Timber.d("category add: error -> ${anError?.errorDetail}")
                    Timber.d("category add: error -> ${anError?.errorBody}")
                }
            })

    }
}
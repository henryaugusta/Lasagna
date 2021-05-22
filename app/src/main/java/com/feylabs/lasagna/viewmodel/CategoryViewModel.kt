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


class CategoryViewModel : ViewModel() {

    val categoryLiveData : MutableLiveData<Resource<ReportCategoryModel>> = MutableLiveData()

    fun getCategory(){
        val gson = Gson()
        AndroidNetworking.get(Endpoint.GET_REPORT_CATEGORY)
            .build()
            .getAsJSONObject(object :JSONObjectRequestListener{

                override fun onResponse(response: JSONObject?) {
                    val myobject: ReportCategoryModel = gson.fromJson(response.toString(), ReportCategoryModel::class.java)
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
}